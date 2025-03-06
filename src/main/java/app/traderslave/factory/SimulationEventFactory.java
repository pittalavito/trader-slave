package app.traderslave.factory;

import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.domain.SimulationOrder;
import lombok.experimental.UtilityClass;
import java.util.UUID;

@UtilityClass
public class SimulationEventFactory {

    public SimulationEvent create(SimulationOrder order) {
        SimulationEvent.EventType eventType = SimulationEvent.EventType.retrieveByOrderStatus(order.getStatus());

        return SimulationEvent.builder()
                .simulationId(order.getSimulationId())
                .balanceUpdates(SimulationEvent.EventType.CREATED_ORDER == eventType ? order.getAmountOfTrade().negate() : order.getProfitLossMinusFees())
                .eventTime(SimulationEvent.EventType.CREATED_ORDER == eventType ? order.getOpenTime() : order.getCloseTime())
                .eventType(eventType)
                .uid(UUID.randomUUID().toString())
                .version(0)
                .build();
    }
}
