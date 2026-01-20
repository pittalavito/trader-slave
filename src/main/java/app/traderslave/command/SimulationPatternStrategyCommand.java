package app.traderslave.command;

import app.traderslave.controller.dto.*;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.OrderType;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.service.DataAnalysesService;
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

    private static final int CANDLES_INTERVAL = 72;

    private static final int LEVERAGE = 10;

    private static final TimeFrame TIME_FRAME = TimeFrame.FIVE_MINUTES;
    private static final CurrencyPair CURRENCY_PAIR = CurrencyPair.SOL_USDC;
    private static final BigDecimal PERCENTAGE_OF_BALANCE_PER_TRADE = BigDecimal.valueOf(0.05);
    private static final BigDecimal TAKE_PROFIT_PERCENTAGE = BigDecimal.valueOf(0.02);
    private static final BigDecimal STOP_LOSS_PERCENTAGE = BigDecimal.valueOf(0.05);

    private static final LocalDateTime SIMULATION_START_TIME = LocalDateTime.of(2026, 1, 1, 0, 0);
    private static final LocalDateTime SIMULATION_END_TIME = LocalDateTime.of(2026, 1, 19, 0, 0);

    private final DataAnalysesService dataAnalysesService;
    private final SimulationService simulationService;

    @Override
    public Mono<CloseSimulationResDto> execute() throws InterruptedException {
        PostSimulationResDto simulation = simulationService.create(createSimulationRequest()).block();
        assert simulation != null;

        PatternDetectionResDto patterns;

        LocalDateTime analysisTime = TimeUtils.calculateEndDate(SIMULATION_START_TIME, TimeFrame.FIVE_MINUTES, CANDLES_INTERVAL);
        PatternDetectionReqDto patternReq = createPatterRequest(analysisTime);

        SimulationOrderResDto openOrder = null;

        int accumulator = 0;
        while (analysisTime.isBefore(SIMULATION_END_TIME)) {
            // Set time frame for pattern detection
            patternReq.setStartTime(TimeUtils.calculateStartDate(analysisTime, TIME_FRAME, CANDLES_INTERVAL));
            patternReq.setEndTime(analysisTime);

            patterns = dataAnalysesService.detectPatterns(patternReq).block();
            assert patterns != null;

            if (openOrder == null) {
                openOrder = processNewOrder(patterns, simulation, analysisTime);
            } else {
                openOrder = processCloseOrder(simulation, openOrder, patterns, analysisTime);
            }
            accumulator++;
            if (accumulator >= 100) {
                log.info("Sleep for a while to avoid rate limit...");
                accumulator = 0;
                Thread.sleep(TimeFrame.ONE_MINUTE.getMillisecond());
            }
            analysisTime = analysisTime.plusMinutes(10);
        }

        return simulationService.close(createCloseSimulationRequest(simulation.getId(), analysisTime));
    }

    private CreateSimulationReqDto createSimulationRequest() {
        CreateSimulationReqDto dto = new CreateSimulationReqDto();
        dto.setCurrencyPair(CURRENCY_PAIR);
        dto.setStartTime(SIMULATION_START_TIME);
        dto.setDescription("Pattern Strategy Simulation");
        return dto;
    }

    private PatternDetectionReqDto createPatterRequest(LocalDateTime endTime) {
        PatternDetectionReqDto dto = new PatternDetectionReqDto();
        dto.setCurrencyPair(CurrencyPair.SOL_USDC);
        dto.setTimeFrame(TimeFrame.FIVE_MINUTES);
        dto.setStartTime(SIMULATION_START_TIME);
        dto.setEndTime(endTime);
        dto.setOnlyBreakoutConfirmed(true);
        return dto;
    }

    private CreateSimulationOrderReqDto createOrderRequest(Long simulationId, OrderType orderType, LocalDateTime localDateTime) {
        CreateSimulationOrderReqDto dto = new CreateSimulationOrderReqDto();
        dto.setSimulationId(simulationId);
        dto.setOrderType(orderType);
        dto.setLeverage(LEVERAGE);
        dto.setMaxAmountOfTrade(false);
        dto.setStartTime(localDateTime);

        BigDecimal balance = simulationService.getBalanceById(simulationId);
        dto.setAmountOfTrade(balance.multiply(PERCENTAGE_OF_BALANCE_PER_TRADE));
        return dto;
    }

    private CloseSimulationOrderReqDto createCloseOrderRequest(Long simulationId, Long orderId, LocalDateTime startTime) {
        CloseSimulationOrderReqDto dto = new CloseSimulationOrderReqDto();
        dto.setSimulationId(simulationId);
        dto.setOrderId(orderId);
        dto.setStartTime(startTime);
        return dto;
    }

    private CloseSimulationReqDto createCloseSimulationRequest(Long simulationId, LocalDateTime localDateTime) {
        CloseSimulationReqDto dto = new CloseSimulationReqDto();
        dto.setSimulationId(simulationId);
        dto.setStartTime(localDateTime);
        return dto;
    }

    private SimulationOrderResDto processNewOrder(PatternDetectionResDto patterns, PostSimulationResDto simulation, LocalDateTime localDateTime) {
        final OrderType orderType;
        switch (SignalUtils.generateLastSignal(patterns)) {
            case BUY -> orderType = OrderType.BUY;
            case SELL -> orderType = OrderType.SELL;
            default -> {
                return null;
            }
        }
        CreateSimulationOrderReqDto orderReq = createOrderRequest(simulation.getId(), orderType, localDateTime);
        return simulationService.createOrder(orderReq).block();
    }

    private SimulationOrderResDto processCloseOrder(PostSimulationResDto simulation, SimulationOrderResDto openOrder, PatternDetectionResDto patterns, LocalDateTime localDateTime) {
        final BigDecimal closePrice = patterns.getClose();
        final BigDecimal takeProfitPrice;
        final BigDecimal stopLossPrice;
        final boolean closeOrder;

        if (OrderType.BUY == openOrder.getOrderType()) {
            takeProfitPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.add(TAKE_PROFIT_PERCENTAGE));
            stopLossPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.subtract(STOP_LOSS_PERCENTAGE));
            closeOrder = closePrice.compareTo(takeProfitPrice) >= 0 || closePrice.compareTo(stopLossPrice) <= 0;
        } else {
            takeProfitPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.subtract(TAKE_PROFIT_PERCENTAGE));
            stopLossPrice = openOrder.getOpenPrice().multiply(BigDecimal.ONE.add(STOP_LOSS_PERCENTAGE));
            closeOrder = closePrice.compareTo(takeProfitPrice) <= 0 || closePrice.compareTo(stopLossPrice) >= 0;
        }

        if (closeOrder) {
            CloseSimulationOrderReqDto closeOrderReq = createCloseOrderRequest(simulation.getId(), openOrder.getOrderId(), localDateTime);
            simulationService.closeOrder(closeOrderReq).block();
            return null;
        }
        return openOrder;
    }
}
