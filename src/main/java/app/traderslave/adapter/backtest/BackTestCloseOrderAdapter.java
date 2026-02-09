package app.traderslave.adapter.backtest;

import app.traderslave.model.dto.req.BackTestCloseOrderReqDto;
import app.traderslave.model.dto.BackTestOrderDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BackTestCloseOrderAdapter {

    public BackTestCloseOrderReqDto adapt(BackTestOrderDto order, LocalDateTime startTime) {
        var closeOrderSimulation = new BackTestCloseOrderReqDto();
        closeOrderSimulation.setOrderId(order.getOrderId());
        closeOrderSimulation.setSimulationId(order.getSimulationId());
        closeOrderSimulation.setStartTime(startTime);
        return closeOrderSimulation;
    }
}
