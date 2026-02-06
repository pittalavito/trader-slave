package app.traderslave.assembler;

import app.traderslave.model.dto.CloseSimulationDto;
import app.traderslave.model.dto.CreateSimulationDto;
import app.traderslave.model.dto.SimulationOrderDto;
import app.traderslave.model.dto.req.TimeReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.domain.model.Simulation;
import app.traderslave.domain.model.SimulationEvent;
import app.traderslave.domain.model.SimulationOrder;
import app.traderslave.utils.ReportUtils;
import app.traderslave.utils.TimeUtils;
import lombok.experimental.UtilityClass;
import java.util.List;
import java.util.Map;

//todo da rifattorizzare dividendo in più assembeler
@UtilityClass
public class SimulationServiceAssembler {

    public CreateSimulationDto toModelCreate(Simulation simulation) {
        return CreateSimulationDto.builder()
                .id(simulation.getId())
                .currencyPair(simulation.getCurrencyPair())
                .description(simulation.getDescription())
                .balance(simulation.getBalance())
                .currency(simulation.getCurrency())
                .build();
    }

    public SimulationOrderDto toModelCreateOrder(SimulationOrder order, TimeReqDto dto) {
        return SimulationOrderDto.builder()
                .requestInfo(buildRequestInfo(dto))
                .orderId(order.getId())
                .simulationId(order.getSimulationId())
                .orderType(order.getType())
                .amountOfTrade(order.getAmountOfTrade())
                .openPrice(order.getOpenPrice())
                .openTime(order.getOpenTime())
                .liquidationPrice(order.getLiquidationPrice())
                .leverage(order.getLeverage())
                .status(SimulationOrderDto.Status.OPEN_NOW)
                .build();
    }

    public SimulationOrderDto toModelCloseOrder(SimulationOrder order, TimeReqDto dto) {
        return SimulationOrderDto.builder()
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

    public CloseSimulationDto toModelClose(Simulation simulation, Map<Long, SimulationOrderDto> ordersIdsMap, Map<SimulationOrderDto.Status, List<Long>> ordersIdsStatusMap, List<SimulationEvent> events) {
        int numOrders = ordersIdsMap.size();
        int numOrdersInProfit = ordersIdsStatusMap.get(SimulationOrderDto.Status.OPEN_WITH_PROFIT).size() + ordersIdsStatusMap.get(SimulationOrderDto.Status.CLOSED_WITH_PROFIT).size();
        int numOrdersInLoss = ordersIdsStatusMap.get(SimulationOrderDto.Status.OPEN_WITH_LOSS).size() + ordersIdsStatusMap.get(SimulationOrderDto.Status.CLOSED_WITH_LOSS).size();
        int numOrderLiquidated = ordersIdsStatusMap.get(SimulationOrderDto.Status.LIQUIDATED).size();
        int numOrderClosedBySimulation = ordersIdsStatusMap.get(SimulationOrderDto.Status.OPEN_NEUTRAL).size() + ordersIdsStatusMap.get(SimulationOrderDto.Status.OPEN_WITH_LOSS).size() + ordersIdsStatusMap.get(SimulationOrderDto.Status.OPEN_WITH_PROFIT).size();
        List<CloseSimulationDto.Event> allEvents = buildEvents(events, ordersIdsMap);

        return CloseSimulationDto.builder()
                .simulationId(simulation.getId())
                .ordersIdsMap(ordersIdsMap)
                //.ordersIdsStatusMap(ordersIdsStatusMap)
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
                .numOrderClosedBySimulation(numOrderClosedBySimulation)
                .percentageOrderProfit(ReportUtils.calculatePercentageNumOrder(numOrders, numOrdersInProfit))
                .percentageNumOrderLoss(ReportUtils.calculatePercentageNumOrder(numOrders, numOrdersInLoss))
                .percentageNumOrderLiquidated(ReportUtils.calculatePercentageNumOrder(numOrders, numOrderLiquidated))
                .percentageNumOrderOpen(ReportUtils.calculatePercentageNumOrder(numOrders, numOrderClosedBySimulation))
                .durationOfSimulationInSeconds(TimeUtils.calculateDiffInSecond(simulation.getStartTime(), simulation.getEndTime()))
                .build();
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private String buildRequestInfo(TimeReqDto dto) {
        return dto.isRealTimeRequest() ? "REAL-TIME-REQUEST" : "BACK-TIME-REQUEST " + dto.getStartTime();
    }

    private SimulationOrderDto.Status mappingStatus(SimulationOrder.Status orderStatus, Boolean isProfit) {
        if (orderStatus == SimulationOrder.Status.LIQUIDATED) {
            return SimulationOrderDto.Status.LIQUIDATED;
        }
        return switch (orderStatus) {
            case OPEN ->
                    mappingStatus(isProfit, SimulationOrderDto.Status.OPEN_WITH_PROFIT, SimulationOrderDto.Status.OPEN_WITH_LOSS, SimulationOrderDto.Status.OPEN_NEUTRAL);
            case CLOSED ->
                    mappingStatus(isProfit, SimulationOrderDto.Status.CLOSED_WITH_PROFIT, SimulationOrderDto.Status.CLOSED_WITH_LOSS, SimulationOrderDto.Status.CLOSED_NEUTRAL);
            default ->
                    throw new CustomException(ExceptionEnum.STATUS_NOT_ALLOWED_FOR_THIS_METHOD);
        };
    }

    private SimulationOrderDto.Status mappingStatus(Boolean isProfit, SimulationOrderDto.Status profitStatus, SimulationOrderDto.Status lossStatus, SimulationOrderDto.Status neutralStatus) {
        if (Boolean.TRUE.equals(isProfit)) {
            return profitStatus;
        } else if (Boolean.FALSE.equals(isProfit)) {
            return lossStatus;
        } else {
            return neutralStatus;
        }
    }

    private List<CloseSimulationDto.Event> buildEvents(List<SimulationEvent> events, Map<Long, SimulationOrderDto> ordersIdsMap) {
        return events.stream()
                .map(event -> buildEvent(event, ordersIdsMap.get(event.getOrderId())))
                .toList();
    }

    private CloseSimulationDto.Event buildEvent(SimulationEvent event, SimulationOrderDto order) {
        boolean isCreatedOrder =  SimulationEvent.EventType.CREATED_ORDER == event.getEventType();

        return CloseSimulationDto.Event.builder()
                .time(event.getEventTime())
                .orderId(event.getOrderId())
                .eventType(event.getEventType())
                .balanceUpdate(event.getBalanceUpdates())
                .orderType(order.getOrderType())
                .openPrice(order.getOpenPrice())
                .openTime(order.getOpenTime())
                .amountOfTrade(order.getAmountOfTrade())
                .leverage(order.getLeverage())
                .closePrice(isCreatedOrder ? null : order.getClosePrice())
                .closeTime(isCreatedOrder ? null : order.getCloseTime())
                .percentageChange(isCreatedOrder ? null : order.getPercentageChange())
                .isProfit(isCreatedOrder ? null : ReportUtils.isProfit(order.getProfitLoss()))
                .build();
    }
}
