package app.traderslave.assembler.backtest;

import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.model.BackTestPortfolioEvent;
import app.traderslave.model.dto.BackTestOrderDto;
import app.traderslave.model.dto.CloseBackTestPortfolioDto;
import app.traderslave.utils.ReportUtils;
import app.traderslave.utils.TimeUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BackTestClosePortfolioAssembler {

    public CloseBackTestPortfolioDto toModel(BackTestPortfolio backTestPortfolio, Map<Long, BackTestOrderDto> ordersIdsMap, Map<BackTestOrderDto.Status, List<Long>> ordersIdsStatusMap, List<BackTestPortfolioEvent> events) {
        int numOrders = ordersIdsMap.size();
        int numOrdersInProfit = ordersIdsStatusMap.get(BackTestOrderDto.Status.OPEN_WITH_PROFIT).size() + ordersIdsStatusMap.get(BackTestOrderDto.Status.CLOSED_WITH_PROFIT).size();
        int numOrdersInLoss = ordersIdsStatusMap.get(BackTestOrderDto.Status.OPEN_WITH_LOSS).size() + ordersIdsStatusMap.get(BackTestOrderDto.Status.CLOSED_WITH_LOSS).size();
        int numOrderLiquidated = ordersIdsStatusMap.get(BackTestOrderDto.Status.LIQUIDATED).size();
        int numOrderClosedBySimulation = ordersIdsStatusMap.get(BackTestOrderDto.Status.OPEN_NEUTRAL).size() + ordersIdsStatusMap.get(BackTestOrderDto.Status.OPEN_WITH_LOSS).size() + ordersIdsStatusMap.get(BackTestOrderDto.Status.OPEN_WITH_PROFIT).size();
        List<CloseBackTestPortfolioDto.Event> allEvents = buildEvents(events, ordersIdsMap);

        return CloseBackTestPortfolioDto.builder()
                .simulationId(backTestPortfolio.getId())
                .ordersIdsMap(ordersIdsMap)
                .events(allEvents)
                .initialBalance(backTestPortfolio.getCurrency().getDefaultCapital())
                .finalBalance(backTestPortfolio.getBalance())
                .balancePercentageChange(ReportUtils.calculatePercentage(backTestPortfolio.getCurrency().getDefaultCapital(), backTestPortfolio.getBalance()))
                //todo .minUnrealizedBalance(null)
                //todo .maxUnrealizedBalance(null)
                .numOrders(numOrders)
                .numOrdersInProfit(numOrdersInProfit)
                .numOrdersInLoss(numOrdersInLoss)
                .numOrderLiquidated(numOrderLiquidated)
                .numOrderClosedBySimulation(numOrderClosedBySimulation)
                .percentageOrderProfit(ReportUtils.calculatePercentageNumOrder(numOrders, numOrdersInProfit))
                .percentageNumOrderLoss(ReportUtils.calculatePercentageNumOrder(numOrders, numOrdersInLoss))
                .percentageNumOrderLiquidated(ReportUtils.calculatePercentageNumOrder(numOrders, numOrderLiquidated))
                .percentageNumOrderOpen(ReportUtils.calculatePercentageNumOrder(numOrders, numOrderClosedBySimulation))
                .durationOfSimulationInSeconds(TimeUtils.calculateDiffInSecond(backTestPortfolio.getStartTime(), backTestPortfolio.getEndTime()))
                .build();
    }

    private List<CloseBackTestPortfolioDto.Event> buildEvents(List<BackTestPortfolioEvent> events, Map<Long, BackTestOrderDto> ordersIdsMap) {
        return events.stream()
                .map(event -> buildEvent(event, ordersIdsMap.get(event.getOrderId())))
                .toList();
    }

    private CloseBackTestPortfolioDto.Event buildEvent(BackTestPortfolioEvent event, BackTestOrderDto order) {
        boolean isCreatedOrder =  BackTestPortfolioEvent.EventType.CREATED_ORDER == event.getEventType();

        return CloseBackTestPortfolioDto.Event.builder()
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
