package app.traderslave.factory;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.model.report.ReportOrder;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.SOrderStatus;
import app.traderslave.utility.ReportUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class SimulationOrderEntityFactory {

    public SimulationOrder create(Simulation simulation, CreateSimulationOrderReqDto dto, CandleResDto candle) {
        return SimulationOrder.builder()
                .simulationId(dto.getSimulationId())
                .amountOfTrade(dto.getAmountOfTrade() == null ? simulation.getBalance() : dto.getAmountOfTrade())
                .openPrice(candle.getClose())
                .openTime(candle.getCloseTime())
                .status(SOrderStatus.OPEN)
                .type(dto.getOrderType())
                .uid(UUID.randomUUID().toString())
                .version(0)
                .liquidationPrice(ReportUtils.calculateLiquidationPrice(candle.getClose(), dto.getOrderType(), dto.getLeverage()))
                .leverage(dto.getLeverage())
                .build();
    }

    public SimulationOrder close(SimulationOrder order, ReportOrder report) {
        order.setStatus(report.isLiquidated() ? SOrderStatus.LIQUIDATED : SOrderStatus.CLOSED);
        order.setClosePrice(report.getClosePrice());
        order.setCloseTime(report.getCloseTime());
        order.setProfitLoss(report.getProfitLoss());
        order.setProfitLossMinusFees(report.getProfitLossMinusFees());
        order.setPercentageChange(report.getPercentageChange());
        order.setMaxUnrealizedProfitDuringTrade(report.getMaxUnrealizedProfitDuringTrade());
        order.setMaxUnrealizedLossDuringTrade(report.getMaxUnrealizedLossDuringTrade());
        order.setDurationOfTradeInSeconds(report.getDurationOfTradeInSeconds());
        order.setLastModificationDate(LocalDateTime.now());
        order.setVersion(order.getVersion() + 1);
        return order;
    }

}
