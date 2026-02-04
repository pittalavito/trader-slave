package app.traderslave.command;

import app.traderslave.assembler.PatternDetectionAssembler;
import app.traderslave.controller.dto.*;
import app.traderslave.model.enums.OrderType;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.service.domain.CandleBackTestDomainService;
import app.traderslave.service.BinanceService;
import app.traderslave.service.manager.SimulationManagerService;
import app.traderslave.service.manager.SimulationOrderManagerService;
import app.traderslave.utility.SignalUtils;
import app.traderslave.utility.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationPatternStrategyCommand extends BaseCommand<SimulationPatterStrategyDto, CloseSimulationResDto> {

    private final SimulationManagerService simulationManagerService;
    private final SimulationOrderManagerService simulationOrderManagerService;
    private final BinanceService binanceService;
    private final CandleBackTestDomainService candleBackTestDomainService;

    @Override
    public CloseSimulationResDto execute() {
        loadData();

        var simulation = initiateSimulation();
        var order = initiateSimulationOrder();

        var analysisTimeReq = initAnalysisEndTime();
        var analysisPatternReq = initiatePatterDetection(analysisTimeReq);

        while (analysisTimeReq.isBefore(requestDto.getSimulationEndTime())) {
            var candles = candleBackTestDomainService.getCandles(analysisPatternReq);
            var patterns = PatternDetectionAssembler.toModelBackTest(candles, analysisPatternReq);

            if (orderIsOpen(order)) {
                order = closeOrder(simulation, order, patterns, analysisTimeReq);
            } else {
                order = openOrder(patterns, simulation, analysisTimeReq);
            }
            analysisTimeReq = analysisTimeReq.plusMinutes(10);
            analysisPatternReq.setStartTime(TimeUtils.calculateStartDate(analysisTimeReq, requestDto.getPatternDetectionTimeFrame(), requestDto.getCandleInterval()));
            analysisPatternReq.setEndTime(analysisTimeReq);
        }
        return closeSimulation(simulation.getId(), analysisTimeReq);
    }

    private void loadData() {
        var request = new CandlesReqDto();
        request.setCurrencyPair(requestDto.getCurrencyPair());
        request.setStartTime(requestDto.getSimulationStartTime());
        request.setEndTime(requestDto.getSimulationEndTime());
        request.setTimeFrame(requestDto.getPatternDetectionTimeFrame());

        if (!CollectionUtils.isEmpty(candleBackTestDomainService.getCandles(request))) {
            return;
        }

        request.setTimeFrame(TimeFrame.ONE_MINUTE);
        binanceService.findCandles(request)
                .doOnNext(response -> candleBackTestDomainService.saveCandleBackTest(request, response))
                .block();

        request.setTimeFrame(TimeFrame.FIVE_MINUTES);
        binanceService.findCandles(request)
                .doOnNext(response -> candleBackTestDomainService.saveCandleBackTest(request, response))
                .block();

        if (TimeFrame.ONE_MINUTE != requestDto.getPatternDetectionTimeFrame() && TimeFrame.FIVE_MINUTES != requestDto.getPatternDetectionTimeFrame()) {
            request.setTimeFrame(requestDto.getPatternDetectionTimeFrame());
            binanceService.findCandles(request)
                    .doOnNext(response -> candleBackTestDomainService.saveCandleBackTest(request, response))
                    .block();
        }
    }

    private CreateSimulationResDto initiateSimulation() {
        CreateSimulationReqDto reqDto = new CreateSimulationReqDto();
        reqDto.setCurrencyPair(requestDto.getCurrencyPair());
        reqDto.setStartTime(requestDto.getSimulationStartTime());
        reqDto.setDescription("Pattern Strategy Simulation");
        return simulationManagerService.create(reqDto);
    }

    private SimulationOrderResDto initiateSimulationOrder() {
        return SimulationOrderResDto.builder().build();
    }

    private LocalDateTime initAnalysisEndTime() {
        var analysisTime = TimeUtils.calculateEndDate(requestDto.getSimulationStartTime(), requestDto.getPatternDetectionTimeFrame(), requestDto.getCandleInterval());
        if (analysisTime.isAfter(requestDto.getSimulationEndTime())) {
            analysisTime = requestDto.getSimulationEndTime().minusSeconds(1);
        }
        return analysisTime;
    }

    private PatternDetectionReqDto initiatePatterDetection(LocalDateTime endTime) {
        PatternDetectionReqDto dto = new PatternDetectionReqDto();
        dto.setCurrencyPair(requestDto.getCurrencyPair());
        dto.setTimeFrame(requestDto.getPatternDetectionTimeFrame());
        dto.setStartTime(requestDto.getSimulationStartTime());
        dto.setEndTime(endTime);
        dto.setOnlyBreakoutConfirmed(true);
        return dto;
    }

    private boolean orderIsOpen(SimulationOrderResDto openOrder) {
        return openOrder != null && openOrder.getOrderId() != null;
    }

    private SimulationOrderResDto openOrder(PatternDetectionResDto patterns, CreateSimulationResDto simulation, LocalDateTime localDateTime) {
        final OrderType orderType;
        switch (SignalUtils.generate(patterns)) {
            case BUY -> orderType = OrderType.BUY;
            case SELL -> orderType = OrderType.SELL;
            default -> {
                return initiateSimulationOrder();
            }
        }
        CreateSimulationOrderReqDto orderReq = createOrderRequest(simulation.getId(), orderType, localDateTime);
        return simulationOrderManagerService.create(orderReq);
    }

    private CreateSimulationOrderReqDto createOrderRequest(Long simulationId, OrderType orderType, LocalDateTime localDateTime) {
        CreateSimulationOrderReqDto dto = new CreateSimulationOrderReqDto();
        dto.setSimulationId(simulationId);
        dto.setOrderType(orderType);
        dto.setLeverage(requestDto.getLeverage());
        dto.setMaxAmountOfTrade(false);
        dto.setStartTime(localDateTime);

        log.info("Creating {} order at {}", orderType, localDateTime);
        BigDecimal balance = simulationManagerService.getBalance(simulationId);
        dto.setAmountOfTrade(balance.multiply(requestDto.getAmountForTradePercentage()));
        return dto;
    }

    private SimulationOrderResDto closeOrder(CreateSimulationResDto simulation, SimulationOrderResDto openOrder, PatternDetectionResDto patterns, LocalDateTime localDateTime) {
        final boolean confirmedTrend = SignalUtils.confirmOrderSignal(openOrder.getOrderType(), patterns);
        final boolean closeOrder;
        final boolean takeProfitHit;
        final boolean stopLossHit;

        final BigDecimal closePrice = patterns.getClose();
        final BigDecimal takeProfitPrice;
        final BigDecimal stopLossPrice;

        // todo aggiungere controllo ema
        if (OrderType.BUY == openOrder.getOrderType()) {
            takeProfitPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.add(requestDto.getTakeProfitPercentage()));
            stopLossPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.subtract(requestDto.getStopLossPercentage()));
            takeProfitHit = closePrice.compareTo(takeProfitPrice) >= 0;
            stopLossHit = closePrice.compareTo(stopLossPrice) <= 0;
        } else {
            takeProfitPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.subtract(requestDto.getTakeProfitPercentage()));
            stopLossPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.add(requestDto.getStopLossPercentage()));
            takeProfitHit = closePrice.compareTo(takeProfitPrice) <= 0;
            stopLossHit = closePrice.compareTo(stopLossPrice) >= 0;
        }

        closeOrder = (stopLossHit) || (takeProfitHit && !confirmedTrend);
        if (closeOrder) {
            log.info("Closing order {}: takeProfitHit={}, stopLossHit={}, confirmedTrend={}", openOrder.getOrderId(), takeProfitHit, stopLossHit, confirmedTrend);
            CloseSimulationOrderReqDto closeOrderReq = createCloseOrderRequest(simulation.getId(), openOrder.getOrderId(), localDateTime);
            simulationOrderManagerService.close(closeOrderReq);
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

    private CloseSimulationResDto closeSimulation(Long simulationId, LocalDateTime localDateTime) {
        CloseSimulationReqDto dto = new CloseSimulationReqDto();
        dto.setSimulationId(simulationId);
        dto.setStartTime(localDateTime);
        return simulationManagerService.close(dto);
    }
}
