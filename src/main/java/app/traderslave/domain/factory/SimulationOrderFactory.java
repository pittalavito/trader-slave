package app.traderslave.domain.factory;

import app.traderslave.model.dto.req.CreateSimulationOrderReqDto;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.dto.OrderReportDto;
import app.traderslave.domain.model.Simulation;
import app.traderslave.domain.model.SimulationOrder;
import app.traderslave.utils.ReportUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class SimulationOrderFactory {

    public SimulationOrder create(Simulation simulation, CreateSimulationOrderReqDto dto, CandleDto candle) {
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

    public SimulationOrder close(SimulationOrder order, OrderReportDto report, boolean endSimulation) {
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
}
