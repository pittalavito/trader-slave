package app.traderslave.adapter;

import app.traderslave.model.dto.SimulationPatterStrategyDto;
import app.traderslave.model.dto.req.CreateSimulationOrderReqDto;
import app.traderslave.model.enums.OrderType;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class CreateSimulationOrderDtoAdapter {

    public CreateSimulationOrderReqDto adapt(SimulationPatterStrategyDto dto, Long simulationId, OrderType orderType, LocalDateTime localDateTime) {
        CreateSimulationOrderReqDto order = new CreateSimulationOrderReqDto();
        order.setSimulationId(simulationId);
        order.setOrderType(orderType);
        order.setLeverage(dto.getLeverage());
        order.setMaxAmountOfTrade(false);
        order.setStartTime(localDateTime);
        return order;
    }
}
