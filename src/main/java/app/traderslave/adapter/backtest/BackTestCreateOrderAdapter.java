package app.traderslave.adapter.backtest;

import app.traderslave.model.dto.SimulationPatterStrategyDto;
import app.traderslave.model.dto.req.BackTestCreateOrderReqDto;
import app.traderslave.model.enums.OrderType;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class BackTestCreateOrderAdapter {

    public BackTestCreateOrderReqDto adapt(SimulationPatterStrategyDto dto, Long simulationId, OrderType orderType, LocalDateTime localDateTime) {
        var order = new BackTestCreateOrderReqDto();
        order.setSimulationId(simulationId);
        order.setOrderType(orderType);
        order.setLeverage(dto.getLeverage());
        order.setMaxAmountOfTrade(false);
        order.setStartTime(localDateTime);
        return order;
    }
}
