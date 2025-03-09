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
                .orderId(order.getId())
                .simulationId(order.getSimulationId())
                .balanceUpdates(SimulationEvent.EventType.CREATED_ORDER == eventType ? order.getAmountOfTrade().negate() : order.getProfitLoss().abs())
                .eventTime(SimulationEvent.EventType.CREATED_ORDER == eventType ? order.getOpenTime() : order.getCloseTime())
                .eventType(eventType)
                .uid(UUID.randomUUID().toString())
                .version(0)
                .build();
    }

    public SimulationEvent closeSImulation(SimulationOrder order) {
        return SimulationEvent.builder()
                .orderId(order.getId())
                .simulationId(order.getSimulationId())
                .balanceUpdates(order.getProfitLoss().abs())
                .eventTime(order.getCloseTime())
                .eventType(SimulationEvent.EventType.CLOSED_SIMULATION)
                .uid(UUID.randomUUID().toString())
                .version(0)
                .build();
    }
}
