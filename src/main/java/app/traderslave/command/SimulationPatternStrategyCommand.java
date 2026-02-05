package app.traderslave.command;

import app.traderslave.command.base.BaseCommand;
import app.traderslave.assembler.PatternDetectionAssembler;
import app.traderslave.controller.dto.*;
import app.traderslave.model.enums.OrderType;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.domain.service.CandleBackTestDomainService;
import app.traderslave.remote.service.BinanceRemoteService;
import app.traderslave.service.simulation.SimulationManagerService;
import app.traderslave.service.simulation.SimulationOrderManagerService;
import app.traderslave.utils.SignalUtils;
import app.traderslave.utils.TimeUtils;
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
    private final BinanceRemoteService binanceRemoteService;
    private final CandleBackTestDomainService candleBackTestDomainService;

    @Override
    public CloseSimulationResDto execute() {
        loadData();

        var simulation = initiateSimulation();
        var order = initiateSimulationOrder();

        var analysisTimeReq = initAnalysisEndTime();
        var analysisPatternReq = initiatePatterDetection(analysisTimeReq);

        while (analysisTimeReq.isBefore(commandRequest.getSimulationEndTime())) {
            var candles = candleBackTestDomainService.getCandles(analysisPatternReq);
            var patterns = PatternDetectionAssembler.toModelBackTest(candles, analysisPatternReq);

            if (orderIsOpen(order)) {
                order = closeOrder(simulation, order, patterns, analysisTimeReq);
            } else {
                order = openOrder(patterns, simulation, analysisTimeReq);
            }
            analysisTimeReq = analysisTimeReq.plusMinutes(10);
            analysisPatternReq.setStartTime(TimeUtils.calculateStartDate(analysisTimeReq, commandRequest.getPatternDetectionTimeFrame(), commandRequest.getCandleInterval()));
            analysisPatternReq.setEndTime(analysisTimeReq);
        }
        return closeSimulation(simulation.getId(), analysisTimeReq);
    }

    private void loadData() {
        var request = new CandlesReqDto();
        request.setCurrencyPair(commandRequest.getCurrencyPair());
        request.setStartTime(commandRequest.getSimulationStartTime());
        request.setEndTime(commandRequest.getSimulationEndTime());
        request.setTimeFrame(commandRequest.getPatternDetectionTimeFrame());

        if (!CollectionUtils.isEmpty(candleBackTestDomainService.getCandles(request))) {
            return;
        }

        request.setTimeFrame(TimeFrame.ONE_MINUTE);
        binanceRemoteService.findCandlesAsync(request)
                .doOnNext(response -> candleBackTestDomainService.saveCandleBackTest(request, response))
                .block();

        request.setTimeFrame(TimeFrame.FIVE_MINUTES);
        binanceRemoteService.findCandlesAsync(request)
                .doOnNext(response -> candleBackTestDomainService.saveCandleBackTest(request, response))
                .block();

        if (TimeFrame.ONE_MINUTE != commandRequest.getPatternDetectionTimeFrame() && TimeFrame.FIVE_MINUTES != commandRequest.getPatternDetectionTimeFrame()) {
            request.setTimeFrame(commandRequest.getPatternDetectionTimeFrame());
            binanceRemoteService.findCandlesAsync(request)
                    .doOnNext(response -> candleBackTestDomainService.saveCandleBackTest(request, response))
                    .block();
        }
    }

    private CreateSimulationResDto initiateSimulation() {
        CreateSimulationReqDto reqDto = new CreateSimulationReqDto();
        reqDto.setCurrencyPair(commandRequest.getCurrencyPair());
        reqDto.setStartTime(commandRequest.getSimulationStartTime());
        reqDto.setDescription("Pattern Strategy Simulation");
        return simulationManagerService.create(reqDto);
    }

    private SimulationOrderResDto initiateSimulationOrder() {
        return SimulationOrderResDto.builder().build();
    }

    private LocalDateTime initAnalysisEndTime() {
        var analysisTime = TimeUtils.calculateEndDate(commandRequest.getSimulationStartTime(), commandRequest.getPatternDetectionTimeFrame(), commandRequest.getCandleInterval());
        if (analysisTime.isAfter(commandRequest.getSimulationEndTime())) {
            analysisTime = commandRequest.getSimulationEndTime().minusSeconds(1);
        }
        return analysisTime;
    }

    private PatternDetectionReqDto initiatePatterDetection(LocalDateTime endTime) {
        PatternDetectionReqDto dto = new PatternDetectionReqDto();
        dto.setCurrencyPair(commandRequest.getCurrencyPair());
        dto.setTimeFrame(commandRequest.getPatternDetectionTimeFrame());
        dto.setStartTime(commandRequest.getSimulationStartTime());
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
        dto.setLeverage(commandRequest.getLeverage());
        dto.setMaxAmountOfTrade(false);
        dto.setStartTime(localDateTime);

        log.info("Creating {} order at {}", orderType, localDateTime);
        BigDecimal balance = BigDecimal.ONE; //todo simulationManagerService.getBalance(simulationId);
        dto.setAmountOfTrade(balance.multiply(commandRequest.getAmountForTradePercentage()));
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
            takeProfitPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.add(commandRequest.getTakeProfitPercentage()));
            stopLossPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.subtract(commandRequest.getStopLossPercentage()));
            takeProfitHit = closePrice.compareTo(takeProfitPrice) >= 0;
            stopLossHit = closePrice.compareTo(stopLossPrice) <= 0;
        } else {
            takeProfitPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.subtract(commandRequest.getTakeProfitPercentage()));
            stopLossPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.add(commandRequest.getStopLossPercentage()));
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
