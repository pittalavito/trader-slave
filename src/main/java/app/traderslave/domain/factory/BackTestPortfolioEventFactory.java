package app.traderslave.domain.factory;

import app.traderslave.domain.model.BackTestPortfolioEvent;
import app.traderslave.domain.model.BackTestOrder;
import lombok.experimental.UtilityClass;
import java.util.UUID;

@UtilityClass
public class BackTestPortfolioEventFactory {

    public BackTestPortfolioEvent create(BackTestOrder order) {
        BackTestPortfolioEvent.EventType eventType = BackTestPortfolioEvent.EventType.retrieveByOrderStatus(order.getStatus());

        return BackTestPortfolioEvent.builder()
                .orderId(order.getId())
                .simulationId(order.getPortfolioId())
                .balanceUpdates(BackTestPortfolioEvent.EventType.CREATED_ORDER == eventType ? order.getAmountOfTrade().negate() : order.getProfitLoss().abs())
                .eventTime(BackTestPortfolioEvent.EventType.CREATED_ORDER == eventType ? order.getOpenTime() : order.getCloseTime())
                .eventType(eventType)
                .uid(UUID.randomUUID().toString())
                .version(0)
                .build();
    }

    public BackTestPortfolioEvent close(BackTestOrder order) {
        return BackTestPortfolioEvent.builder()
                .orderId(order.getId())
                .simulationId(order.getPortfolioId())
                .balanceUpdates(order.getProfitLoss().abs())
                .eventTime(order.getCloseTime())
                .eventType(BackTestPortfolioEvent.EventType.CLOSED_SIMULATION)
                .uid(UUID.randomUUID().toString())
                .version(0)
                .build();
    }
}
