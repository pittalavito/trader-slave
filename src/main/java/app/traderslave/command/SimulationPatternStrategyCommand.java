package app.traderslave.command;

import app.traderslave.adapter.*;
import app.traderslave.command.base.BaseCommand;
import app.traderslave.assembler.PatternDetectionDtoAssembler;
import app.traderslave.model.dto.SimulationPatterStrategyDto;
import app.traderslave.model.dto.req.*;
import app.traderslave.model.dto.CloseSimulationDto;
import app.traderslave.model.dto.CreateSimulationDto;
import app.traderslave.model.dto.PatternDetectionDto;
import app.traderslave.model.dto.SimulationOrderDto;
import app.traderslave.model.enums.OrderType;
import app.traderslave.domain.service.CandleBackTestDomainService;
import app.traderslave.model.enums.Signal;
import app.traderslave.service.simulation.SimulationManagerService;
import app.traderslave.service.simulation.SimulationOrderManagerService;
import app.traderslave.utils.SignalUtils;
import app.traderslave.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationPatternStrategyCommand extends BaseCommand<SimulationPatterStrategyDto, CloseSimulationDto> {

    private final SimulationManagerService simulationManagerService;
    private final SimulationOrderManagerService simulationOrderManagerService;
    private final CandleBackTestDomainService candleBackTestDomainService;
    private final SaveCandleBackTestCommand saveCandleBackTestCommand;

    @Override
    public CloseSimulationDto execute() {
        loadData();

        var simulation = createSimulation();
        var order = initOrder();

        var analysisTimeReq = initAnalysisTime();
        var analysisPatternReq = initiatePatterDetection(analysisTimeReq);

        while (analysisTimeReq.isBefore(commandRequest.getSimulationEndTime())) {

            var candles = candleBackTestDomainService.getCandles(analysisPatternReq);
            var patterns = PatternDetectionDtoAssembler.toModelBackTest(candles, analysisPatternReq);

            if (orderIsOpen(order)) {
                order = closeOrder(order, patterns, analysisTimeReq);
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
        var request = CandleDtoAdapter.adapt(commandRequest);
        saveCandleBackTestCommand.setCommandRequest(request);
        saveCandleBackTestCommand.execute().block();
    }

    private CreateSimulationDto createSimulation() {
        var request = CreateSimulationDtoAdapter.adapt(commandRequest);
        return simulationManagerService.create(request);
    }

    private SimulationOrderDto initOrder() {
        return SimulationOrderDto.builder().build();
    }

    private LocalDateTime initAnalysisTime() {
        var analysisTime = TimeUtils.calculateEndDate(commandRequest.getSimulationStartTime(), commandRequest.getPatternDetectionTimeFrame(), commandRequest.getCandleInterval());
        if (analysisTime.isAfter(commandRequest.getSimulationEndTime())) {
            analysisTime = commandRequest.getSimulationEndTime().minusSeconds(1);
        }
        return analysisTime;
    }

    private PatternDetectionReqDto initiatePatterDetection(LocalDateTime endTime) {
        return PatternDetectionDtoAdapter.adapt(commandRequest, endTime, true);
    }

    private boolean orderIsOpen(SimulationOrderDto openOrder) {
        return openOrder != null && openOrder.getOrderId() != null;
    }

    private SimulationOrderDto openOrder(PatternDetectionDto patterns, CreateSimulationDto simulation, LocalDateTime localDateTime) {
        final Long simulationId = simulation.getId();
        final Signal signal = SignalUtils.generate(patterns);
        final OrderType orderType = OrderType.map(signal);

        if (orderType == null) {
            return initOrder();
        }

        CreateSimulationOrderReqDto orderReq = CreateSimulationOrderDtoAdapter.adapt(commandRequest, simulationId, orderType, localDateTime);
        log.info("Creating {} order at {}", orderType, localDateTime);
        BigDecimal balance = simulationManagerService.getBalance(simulationId);
        orderReq.setAmountOfTrade(balance.multiply(commandRequest.getAmountForTradePercentage()));
        return simulationOrderManagerService.create(orderReq);
    }


    private SimulationOrderDto closeOrder(SimulationOrderDto openOrder, PatternDetectionDto patterns, LocalDateTime localDateTime) {
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
            closeOrder(openOrder, localDateTime);
            return null;
        }
        return openOrder;
    }

    private void closeOrder(SimulationOrderDto orderDto, LocalDateTime localDateTime) {
        CloseSimulationOrderReqDto closeOrderReq = CloseSimulationOrderAdapter.adapt(orderDto, localDateTime);
        simulationOrderManagerService.close(closeOrderReq);
    }

    private CloseSimulationDto closeSimulation(Long simulationId, LocalDateTime localDateTime) {
        var dto = CloseSimulationDtoAdapter.adapt(simulationId, localDateTime);
        return simulationManagerService.close(dto);
    }
}
