package app.traderslave.adapter;

import app.traderslave.model.dto.req.CloseSimulationOrderReqDto;
import app.traderslave.model.dto.SimulationOrderDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class CloseSimulationOrderAdapter {

    public CloseSimulationOrderReqDto adapt(SimulationOrderDto order, LocalDateTime startTime) {
        var closeOrderSimulation = new CloseSimulationOrderReqDto();
        closeOrderSimulation.setOrderId(order.getOrderId());
        closeOrderSimulation.setSimulationId(order.getSimulationId());
        closeOrderSimulation.setStartTime(startTime);
        return closeOrderSimulation;
    }
}
