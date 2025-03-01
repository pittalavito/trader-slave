package app.traderslave.factory;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.model.domain.ReportOrder;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.OrderType;
import app.traderslave.model.enums.SOrderStatus;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
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
                .liquidationPrice(calculateLiquidationPrice(candle.getClose(), dto.getOrderType(), dto.getLeverage()))
                .leverage(dto.getLeverage())
                .build();
    }

    private BigDecimal calculateLiquidationPrice(BigDecimal currentPrice, OrderType type, int leverage) {
        BigDecimal percentage;
        if (OrderType.BUY.equals(type)) {
            percentage = BigDecimal.valueOf(1 - 0.95/leverage);
        } else {
            percentage = BigDecimal.valueOf(1 + 0.95/leverage);
        }
        return currentPrice.multiply(percentage);
    }

    public SimulationOrder close(SimulationOrder order, CandlesResDto candles) {
        ReportOrder report = ReportOrderFactory.create(order, candles);

        order.setStatus(report.isLiquidated() ? SOrderStatus.LIQUIDATED : SOrderStatus.CLOSE);
        order.setReport(report);
        order.setLastModificationDate(LocalDateTime.now());
        order.setVersion(order.getVersion() + 1);
        return order;
    }

}
