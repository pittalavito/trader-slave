package app.traderslave.assembler;

import app.traderslave.controller.dto.*;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.utility.ReportUtils;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import java.util.List;
import java.util.Map;

@UtilityClass
public class SimulationServiceAssembler {

    public PostSimulationResDto toModelCreate(Simulation simulation) {
        return PostSimulationResDto.builder()
                .simulationId(simulation.getId())
                .currencyPair(simulation.getCurrencyPair())
                .description(simulation.getDescription())
                .balance(simulation.getBalance())
                .currency(simulation.getCurrency())
                .build();
    }

    public SimulationOrderResDto toModelCreateOrder(SimulationOrder order, TimeReqDto dto) {
        return SimulationOrderResDto.builder()
                .requestInfo(buildRequestInfo(dto))
                .orderId(order.getId())
                .simulationId(order.getSimulationId())
                .orderType(order.getType())
                .amountOfTrade(order.getAmountOfTrade())
                .openPrice(order.getOpenPrice())
                .openTime(order.getOpenTime())
                .liquidationPrice(order.getLiquidationPrice())
                .leverage(order.getLeverage())
                .status(SimulationOrderResDto.Status.OPEN_NOW)
                .build();
    }

    public SimulationOrderResDto toModelCloseOrder(SimulationOrder order, TimeReqDto dto) {
        Assert.isTrue(SimulationOrder.Status.CLOSED == order.getStatus() || SimulationOrder.Status.LIQUIDATED == order.getStatus(), "Status order cannot be OPEN");
        return SimulationOrderResDto.builder()
                .requestInfo(buildRequestInfo(dto))
                .orderType(order.getType())
                .amountOfTrade(order.getAmountOfTrade())
                .simulationId(order.getSimulationId())
                .orderId(order.getId())
                .status(mappingStatus(order.getStatus(), ReportUtils.isProfit(order)))
                .openPrice(order.getOpenPrice())
                .closePrice(order.getClosePrice())
                .openTime(order.getOpenTime())
                .closeTime(order.getCloseTime())
                .leverage(order.getLeverage())
                .liquidationPrice(order.getLiquidationPrice())
                .profitLoss(order.getProfitLoss())
                .percentageChange(order.getPercentageChange())
                .maxUnrealizedLossDuringTrade(order.getMaxUnrealizedLossDuringTrade())
                .maxUnrealizedProfitDuringTrade(order.getMaxUnrealizedProfitDuringTrade())
                .durationOfTradeInSeconds(order.getDurationOfTradeInSeconds())
                .percentageChange(order.getPercentageChange())
                .maxPriceDuringTrade(order.getMaxPriceDuringTrade())
                .minPriceDuringTrade(order.getMinPriceDuringTrade())
                .build();
    }

    public CloseSimulationResDto toModelClose(Simulation simulation, Map<Long, SimulationOrderResDto> ordersIdsMap, Map<SimulationOrderResDto.Status, List<Long>> ordersIdsStatusMap, List<SimulationEvent> events) {
        int numOrders = ordersIdsMap.size();
        int numOrdersInProfit = ordersIdsStatusMap.get(SimulationOrderResDto.Status.OPEN_WITH_PROFIT).size() + ordersIdsStatusMap.get(SimulationOrderResDto.Status.CLOSED_WITH_PROFIT).size();
        int numOrdersInLoss = ordersIdsStatusMap.get(SimulationOrderResDto.Status.OPEN_WITH_LOSS).size() + ordersIdsStatusMap.get(SimulationOrderResDto.Status.CLOSED_WITH_LOSS).size();
        int numOrderLiquidated = ordersIdsStatusMap.get(SimulationOrderResDto.Status.LIQUIDATED).size();
        int numOrderOpen = ordersIdsStatusMap.get(SimulationOrderResDto.Status.OPEN_NEUTRAL).size() + ordersIdsStatusMap.get(SimulationOrderResDto.Status.OPEN_WITH_LOSS).size() + ordersIdsStatusMap.get(SimulationOrderResDto.Status.OPEN_WITH_PROFIT).size();
        List<CloseSimulationResDto.Event> allEvents = buildEvents(events, ordersIdsMap);

        return CloseSimulationResDto.builder()
                .simulationId(simulation.getId())
                .ordersIdsMap(ordersIdsMap)
                .ordersIdsStatusMap(ordersIdsStatusMap)
                .events(allEvents)
                .initialBalance(simulation.getCurrency().getDefaultCapital())
                .finalBalance(simulation.getBalance())
                .balancePercentageChange(ReportUtils.calculatePercentage(simulation.getCurrency().getDefaultCapital(), simulation.getBalance()))
                .minUnrealizedBalance(null)
                .maxUnrealizedBalance(null)
                .numOrders(numOrders)
                .numOrdersInProfit(numOrdersInProfit)
                .numOrdersInLoss(numOrdersInLoss)
                .numOrderLiquidated(numOrderLiquidated)
                .numOrderOpen(numOrderOpen)
                .percentageOrderProfit(ReportUtils.calculatePercentageNumOrder(numOrders, numOrdersInProfit))
                .percentageNumOrderLoss(ReportUtils.calculatePercentageNumOrder(numOrders, numOrdersInLoss))
                .percentageNumOrderLiquidated(ReportUtils.calculatePercentageNumOrder(numOrders, numOrderLiquidated))
                .percentageNumOrderOpen(ReportUtils.calculatePercentageNumOrder(numOrders, numOrderOpen))
                .durationOfSimulationInSeconds(TimeUtils.calculateDiffInSecond(simulation.getStartTime(), simulation.getEndTime()))
                .build();
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private String buildRequestInfo(TimeReqDto dto) {
        return dto.isRealTimeRequest() ? "REAL-TIME-REQUEST" : "BACK-TIME-REQUEST " + dto.getStartTime();
    }

    private SimulationOrderResDto.Status mappingStatus(SimulationOrder.Status orderStatus, Boolean isProfit) {
        if (orderStatus == SimulationOrder.Status.LIQUIDATED) {
            return SimulationOrderResDto.Status.LIQUIDATED;
        }
        return switch (orderStatus) {
            case OPEN ->
                    mappingStatus(isProfit, SimulationOrderResDto.Status.OPEN_WITH_PROFIT, SimulationOrderResDto.Status.OPEN_WITH_LOSS, SimulationOrderResDto.Status.OPEN_NEUTRAL);
            case CLOSED ->
                    mappingStatus(isProfit, SimulationOrderResDto.Status.CLOSED_WITH_PROFIT, SimulationOrderResDto.Status.CLOSED_WITH_LOSS, SimulationOrderResDto.Status.CLOSED_NEUTRAL);
            default ->
                    throw new CustomException(ExceptionEnum.STATUS_NOT_ALLOWED_FOR_THIS_METHOD);
        };
    }

    private SimulationOrderResDto.Status mappingStatus(Boolean isProfit, SimulationOrderResDto.Status profitStatus, SimulationOrderResDto.Status lossStatus, SimulationOrderResDto.Status neutralStatus) {
        if (Boolean.TRUE.equals(isProfit)) {
            return profitStatus;
        } else if (Boolean.FALSE.equals(isProfit)) {
            return lossStatus;
        } else {
            return neutralStatus;
        }
    }

    private List<CloseSimulationResDto.Event> buildEvents(List<SimulationEvent> events, Map<Long, SimulationOrderResDto> ordersIdsMap) {
        return events.stream()
                .map(event -> buildEvent(event, ordersIdsMap.get(event.getOrderId())))
                .toList();
    }

    private CloseSimulationResDto.Event buildEvent(SimulationEvent event, SimulationOrderResDto order) {
        return CloseSimulationResDto.Event.builder()
                .time(event.getEventTime())
                .orderId(event.getOrderId())
                .eventType(event.getEventType())
                .balanceUpdate(event.getBalanceUpdates())
                .orderType(order.getOrderType())
                .openPrice(order.getOpenPrice())
                .closePrice(order.getClosePrice())
                .openTime(order.getOpenTime())
                .closeTime(order.getCloseTime())
                .leverage(order.getLeverage())
                .amountOfTrade(order.getAmountOfTrade())
                .percentageChange(order.getPercentageChange())
                .isProfit(null)
                .build();
    }
}
