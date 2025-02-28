package app.traderslave.factory;

import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.OrderType;
import app.traderslave.model.enums.SOrderStatus;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class SimulationOrderEntityFactory {

    public SimulationOrder create(Long simulationId, OrderType orderType, BigDecimal amountOfTrade, BigDecimal openPrice, LocalDateTime openTime) {
        return SimulationOrder.builder()
                .simulationId(simulationId)
                .amountOfTrade(amountOfTrade)
                .openPrice(openPrice)
                .openTime(openTime)
                .status(SOrderStatus.OPEN)
                .type(orderType)
                .uid(UUID.randomUUID().toString())
                .version(0)
                .extraInfo(null)
                .build();
    }
}
