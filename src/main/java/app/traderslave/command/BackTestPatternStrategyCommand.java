package app.traderslave.command;

import app.traderslave.adapter.*;
import app.traderslave.adapter.backtest.BackTestCloseOrderAdapter;
import app.traderslave.adapter.backtest.BackTestClosePortfolioAdapter;
import app.traderslave.adapter.backtest.BackTestCreateOrderAdapter;
import app.traderslave.adapter.backtest.BackTestCreatePortfolioAdapter;
import app.traderslave.assembler.CandleAssembler;
import app.traderslave.command.backtest.BackTestSaveCandlesCommand;
import app.traderslave.command.base.BaseCommand;
import app.traderslave.assembler.PatternDetectionAssembler;
import app.traderslave.model.dto.*;
import app.traderslave.model.dto.req.*;
import app.traderslave.model.enums.OrderType;
import app.traderslave.domain.service.BackTestCandleDomainService;
import app.traderslave.model.enums.Signal;
import app.traderslave.service.BackTestPortfolioService;
import app.traderslave.service.BackTestOrderService;
import app.traderslave.utils.SignalUtils;
import app.traderslave.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
//todo da usare come base per creare le classi del bot
public class BackTestPatternStrategyCommand extends BaseCommand<SimulationPatterStrategyDto, CloseBackTestPortfolioDto> {

    private final BackTestPortfolioService backTestPortfolioService;
    private final BackTestOrderService backTestOrderService;
    private final BackTestCandleDomainService backTestCandleDomainService;
    private final BackTestSaveCandlesCommand backTestSaveCandlesCommand;

    private final CandleAdapter candleAdapter;
    private final BackTestCreatePortfolioAdapter backTestCreatePortfolioAdapter;
    private final BackTestCreateOrderAdapter backTestCreateOrderAdapter;
    private final BackTestCloseOrderAdapter backTestCloseOrderAdapter;
    private final PatternDetectionAdapter patternDetectionAdapter;
    private final BackTestClosePortfolioAdapter backTestClosePortfolioAdapter;

    private final CandleAssembler candleAssembler;
    private final PatternDetectionAssembler patternDetectionAssembler;

    @Override
    public CloseBackTestPortfolioDto execute() {
        loadData();

        var simulation = createSimulation();
        var order = initOrder();

        var analysisTimeReq = initAnalysisTime();
        var analysisPatternReq = initPatterDetectionReq(analysisTimeReq);

        while (analysisTimeReq.isBefore(commandRequest.getSimulationEndTime())) {

            var marketData = getMarketData(analysisPatternReq);
            var tradingSignals = getTradingSignal(analysisPatternReq, marketData);

            if (orderIsOpen(order)) {
                order = closeOrder(order, tradingSignals, analysisTimeReq);
            } else {
                order = openOrder(tradingSignals, simulation, analysisTimeReq);
            }
            nextPatternDetectionReq(analysisPatternReq, analysisTimeReq);
        }

        return closeSimulation(simulation.getId(), analysisTimeReq);
    }

    private void loadData() {
        var request = candleAdapter.adapt(commandRequest);
        backTestSaveCandlesCommand.setCommandRequest(request);
        backTestSaveCandlesCommand.execute().block();
    }

    private BackTestCreatePortfolioDto createSimulation() {
        var request = backTestCreatePortfolioAdapter.adapt(commandRequest);
        return backTestPortfolioService.create(request);
    }

    private BackTestOrderDto initOrder() {
        return BackTestOrderDto.builder().build();
    }

    private LocalDateTime initAnalysisTime() {
        var analysisTime = TimeUtils.calculateEndDate(commandRequest.getSimulationStartTime(), commandRequest.getPatternDetectionTimeFrame(), commandRequest.getCandleInterval());
        if (analysisTime.isAfter(commandRequest.getSimulationEndTime())) {
            analysisTime = commandRequest.getSimulationEndTime().minusSeconds(1);
        }
        return analysisTime;
    }

    private PatternDetectionReqDto initPatterDetectionReq(LocalDateTime endTime) {
        return patternDetectionAdapter.adapt(commandRequest, endTime, true);
    }

    private List<CandleDto> getMarketData(PatternDetectionReqDto analysisPatternReq) {
        var candles = backTestCandleDomainService.getCandles(analysisPatternReq);
        return candleAssembler.toModel(candles);
    }

    private PatternDetectionDto getTradingSignal(PatternDetectionReqDto analysisPatternReq, List<CandleDto> marketData) {
        return patternDetectionAssembler.toModel(marketData, analysisPatternReq);
    }

    private boolean orderIsOpen(BackTestOrderDto openOrder) {
        return openOrder != null && openOrder.getOrderId() != null;
    }

    private BackTestOrderDto openOrder(PatternDetectionDto patterns, BackTestCreatePortfolioDto simulation, LocalDateTime localDateTime) {
        final Long simulationId = simulation.getId();
        final Signal signal = SignalUtils.generate(patterns);
        final OrderType orderType = OrderType.map(signal);

        if (orderType == null) {
            return initOrder();
        }

        BackTestCreateOrderReqDto orderReq = backTestCreateOrderAdapter.adapt(commandRequest, simulationId, orderType, localDateTime);
        log.info("Creating {} order at {}", orderType, localDateTime);
        BigDecimal balance = backTestPortfolioService.getBalance(simulationId);
        orderReq.setAmountOfTrade(balance.multiply(commandRequest.getAmountForTradePercentage()));
        return backTestOrderService.create(orderReq);
    }

    private BackTestOrderDto closeOrder(BackTestOrderDto openOrder, PatternDetectionDto patterns, LocalDateTime localDateTime) {
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

    private void nextPatternDetectionReq(PatternDetectionReqDto currentReq, LocalDateTime analysisTimeReq){
        analysisTimeReq = analysisTimeReq.plusMinutes(10);
        currentReq.setStartTime(TimeUtils.calculateStartDate(analysisTimeReq, commandRequest.getPatternDetectionTimeFrame(), commandRequest.getCandleInterval()));
        currentReq.setEndTime(analysisTimeReq);
    }

    private void closeOrder(BackTestOrderDto orderDto, LocalDateTime localDateTime) {
        BackTestCloseOrderReqDto closeOrderReq = backTestCloseOrderAdapter.adapt(orderDto, localDateTime);
        backTestOrderService.close(closeOrderReq);
    }

    private CloseBackTestPortfolioDto closeSimulation(Long simulationId, LocalDateTime localDateTime) {
        var dto = backTestClosePortfolioAdapter.adapt(simulationId, localDateTime);
        return backTestPortfolioService.close(dto);
    }
}
