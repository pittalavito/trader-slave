package app.traderslave.factory;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.model.report.OrderReport;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.utility.ReportUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class SimulationOrderFactory {

    public SimulationOrder create(Simulation simulation, CreateSimulationOrderReqDto dto, CandleResDto candle) {
        return SimulationOrder.builder()
                .simulationId(dto.getSimulationId())
                .amountOfTrade(dto.getAmountOfTrade() == null ? simulation.getBalance() : dto.getAmountOfTrade())
                .openPrice(candle.getClose())
                .openTime(candle.getCloseTime())
                .status(SimulationOrder.Status.OPEN)
                .type(dto.getOrderType())
                .uid(UUID.randomUUID().toString())
                .version(0)
                .liquidationPrice(ReportUtils.calculateLiquidationPrice(candle.getClose(), dto.getOrderType(), dto.getLeverage()))
                .leverage(dto.getLeverage())
                .build();
    }

    public SimulationOrder close(SimulationOrder order, OrderReport report, boolean endSimulation) {
        if(!endSimulation) {
            order.setStatus(report.isLiquidated() ? SimulationOrder.Status.LIQUIDATED : SimulationOrder.Status.CLOSED);
        }
        order.setClosePrice(report.getClosePrice());
        order.setCloseTime(report.getCloseTime());
        order.setProfitLoss(report.getProfitLoss());
        order.setPercentageChange(report.getPercentageChange());
        order.setMaxUnrealizedProfitDuringTrade(report.getMaxUnrealizedProfitDuringTrade());
        order.setMaxUnrealizedLossDuringTrade(report.getMaxUnrealizedLossDuringTrade());
        order.setDurationOfTradeInSeconds(report.getDurationOfTradeInSeconds());
        order.setLastModificationDate(LocalDateTime.now());
        order.setVersion(order.getVersion() + 1);
        order.setMaxPriceDuringTrade(report.getMaxPriceDuringTrade());
        order.setMinPriceDuringTrade(report.getMinPriceDuringTrade());
        return order;
    }

    public SimulationOrder closeBySimulationClose(SimulationOrder order, OrderReport report) {
        order.setStatus(SimulationOrder.Status.OPEN);
        order.setClosePrice(report.getClosePrice());
        order.setCloseTime(report.getCloseTime());
        order.setProfitLoss(report.getProfitLoss());
        order.setPercentageChange(report.getPercentageChange());
        order.setMaxUnrealizedProfitDuringTrade(report.getMaxUnrealizedProfitDuringTrade());
        order.setMaxUnrealizedLossDuringTrade(report.getMaxUnrealizedLossDuringTrade());
        order.setDurationOfTradeInSeconds(report.getDurationOfTradeInSeconds());
        order.setLastModificationDate(LocalDateTime.now());
        order.setVersion(order.getVersion() + 1);
        order.setMaxPriceDuringTrade(report.getMaxPriceDuringTrade());
        order.setMinPriceDuringTrade(report.getMinPriceDuringTrade());
        return order;
    }

}
