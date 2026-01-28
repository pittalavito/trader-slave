package app.traderslave.command;

import app.traderslave.assembler.PatternDetectionAssembler;
import app.traderslave.controller.dto.*;
import app.traderslave.model.enums.OrderType;
import app.traderslave.service.BinanceCandleBackTestService;
import app.traderslave.service.BinanceService;
import app.traderslave.service.simulation.SimulationService;
import app.traderslave.utility.SignalUtils;
import app.traderslave.utility.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationPatternStrategyCommand extends BaseMonoCommand<Void, CloseSimulationResDto> {

    private final SimulationService simulationService;
    private final BinanceService binanceService;
    private final BinanceCandleBackTestService binanceCandleBackTestService;

    private final SimulationPatterStrategyDto params = new SimulationPatterStrategyDto();


    @Override
    public Mono<CloseSimulationResDto> execute() throws InterruptedException {
        loadData();

        var simulation = initiateSimulation();
        var order = initiateSimulationOrder();

        var analysisTimeReq = initAnalysisEndTime();
        var analysisPatternReq = initiatePatterDetection(analysisTimeReq);

        while (analysisTimeReq.isBefore(params.getSimulationEndTime())) {
            var candles = binanceCandleBackTestService.getCandles(analysisPatternReq);
            var patterns = PatternDetectionAssembler.toModelBackTest(candles, analysisPatternReq);

            if (orderIsOpen(order)) {
                order = closeOrder(simulation, order, patterns, analysisTimeReq);
            } else {
                order = openOrder(patterns, simulation, analysisTimeReq);
            }
            analysisTimeReq = analysisTimeReq.plusMinutes(10);
            analysisPatternReq.setStartTime(TimeUtils.calculateStartDate(analysisTimeReq, params.getPatternDetectionTimeFrame(), params.getCandleInterval()));
            analysisPatternReq.setEndTime(analysisTimeReq);
        }
        return closeSimulation(simulation.getId(), analysisTimeReq);
    }

    private void loadData() {
        var request = new CandlesReqDto();
        request.setCurrencyPair(params.getCurrencyPair());
        request.setStartTime(params.getSimulationStartTime());
        request.setEndTime(params.getSimulationEndTime());

        // pattern detection candles backtest load
        request.setTimeFrame(params.getPatternDetectionTimeFrame());
        binanceService.findCandles(request)
                .doOnNext(response -> binanceCandleBackTestService.saveCandleBackTest(request, response))
                .block();

       // // ema candles backtest load
       // request.setTimeFrame(params.getEmaAnalysisTimeFrame());
       // candlesResponse = binanceService.findCandles(request).block();
       // if (candlesResponse != null) {
       //     binanceCandleBackTestService.saveCandleBackTest(request, candlesResponse);
       // }
    }

    private PostSimulationResDto initiateSimulation() {
        CreateSimulationReqDto reqDto = new CreateSimulationReqDto();
        reqDto.setCurrencyPair(params.getCurrencyPair());
        reqDto.setStartTime(params.getSimulationStartTime());
        reqDto.setDescription("Pattern Strategy Simulation");

        PostSimulationResDto resDto = simulationService.create(reqDto).block();
        assert resDto != null;
        return resDto;
    }

    private SimulationOrderResDto initiateSimulationOrder() {
        return SimulationOrderResDto.builder().build();
    }

    private LocalDateTime initAnalysisEndTime() {
        var analysisTime = TimeUtils.calculateEndDate(params.getSimulationStartTime(), params.getPatternDetectionTimeFrame(), params.getCandleInterval());
        if (analysisTime.isAfter(params.getSimulationEndTime())) {
            analysisTime = params.getSimulationEndTime().minusSeconds(1);
        }
        return analysisTime;
    }

    private PatternDetectionReqDto initiatePatterDetection(LocalDateTime endTime) {
        PatternDetectionReqDto dto = new PatternDetectionReqDto();
        dto.setCurrencyPair(params.getCurrencyPair());
        dto.setTimeFrame(params.getPatternDetectionTimeFrame());
        dto.setStartTime(params.getSimulationStartTime());
        dto.setEndTime(endTime);
        dto.setOnlyBreakoutConfirmed(true);
        return dto;
    }

    private boolean orderIsOpen(SimulationOrderResDto openOrder) {
        return openOrder != null && openOrder.getOrderId() != null;
    }

    private SimulationOrderResDto openOrder(PatternDetectionResDto patterns, PostSimulationResDto simulation, LocalDateTime localDateTime) {
        final OrderType orderType;
        switch (SignalUtils.generate(patterns)) {
            case BUY -> orderType = OrderType.BUY;
            case SELL -> orderType = OrderType.SELL;
            default -> {
                return initiateSimulationOrder();
            }
        }
        CreateSimulationOrderReqDto orderReq = createOrderRequest(simulation.getId(), orderType, localDateTime);
        return simulationService.createOrder(orderReq).block();
    }

    private CreateSimulationOrderReqDto createOrderRequest(Long simulationId, OrderType orderType, LocalDateTime localDateTime) {
        CreateSimulationOrderReqDto dto = new CreateSimulationOrderReqDto();
        dto.setSimulationId(simulationId);
        dto.setOrderType(orderType);
        dto.setLeverage(params.getLeverage());
        dto.setMaxAmountOfTrade(false);
        dto.setStartTime(localDateTime);

        log.info("Creating {} order at {}", orderType, localDateTime);
        BigDecimal balance = simulationService.getBalance(simulationId);
        dto.setAmountOfTrade(balance.multiply(params.getAmountForTradePercentage()));
        return dto;
    }

    private SimulationOrderResDto closeOrder(PostSimulationResDto simulation, SimulationOrderResDto openOrder, PatternDetectionResDto patterns, LocalDateTime localDateTime) {
        final boolean confirmedTrend = SignalUtils.confirmOrderSignal(openOrder.getOrderType(), patterns);
        final boolean closeOrder;
        final boolean takeProfitHit;
        final boolean stopLossHit;

        final BigDecimal closePrice = patterns.getClose();
        final BigDecimal takeProfitPrice;
        final BigDecimal stopLossPrice;

        // todo aggiungere controllo ema
        if (OrderType.BUY == openOrder.getOrderType()) {
            takeProfitPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.add(params.getTakeProfitPercentage()));
            stopLossPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.subtract(params.getStopLossPercentage()));
            takeProfitHit = closePrice.compareTo(takeProfitPrice) >= 0;
            stopLossHit = closePrice.compareTo(stopLossPrice) <= 0;
        } else {
            takeProfitPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.subtract(params.getTakeProfitPercentage()));
            stopLossPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.add(params.getStopLossPercentage()));
            takeProfitHit = closePrice.compareTo(takeProfitPrice) <= 0;
            stopLossHit = closePrice.compareTo(stopLossPrice) >= 0;
        }

        closeOrder = (stopLossHit) || (takeProfitHit && !confirmedTrend);
        if (closeOrder) {
            log.info("Closing order {}: takeProfitHit={}, stopLossHit={}, confirmedTrend={}", openOrder.getOrderId(), takeProfitHit, stopLossHit, confirmedTrend);
            CloseSimulationOrderReqDto closeOrderReq = createCloseOrderRequest(simulation.getId(), openOrder.getOrderId(), localDateTime);
            simulationService.closeOrder(closeOrderReq).block();
            return null;
        }
        return openOrder;
    }

    private CloseSimulationOrderReqDto createCloseOrderRequest(Long simulationId, Long orderId, LocalDateTime startTime) {
        CloseSimulationOrderReqDto dto = new CloseSimulationOrderReqDto();
        dto.setSimulationId(simulationId);
        dto.setOrderId(orderId);
        dto.setStartTime(startTime);
        return dto;
    }

    private Mono<CloseSimulationResDto> closeSimulation(Long simulationId, LocalDateTime localDateTime) {
        CloseSimulationReqDto dto = new CloseSimulationReqDto();
        dto.setSimulationId(simulationId);
        dto.setStartTime(localDateTime);
        return simulationService.close(dto);
    }
}
