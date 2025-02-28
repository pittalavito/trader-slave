package app.traderslave.factory;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.SOrderStatus;
import lombok.experimental.UtilityClass;
import java.util.UUID;

@UtilityClass
public class SimulationOrderEntityFactory {

    public SimulationOrder create(Simulation simulation, CreateSimulationOrderReqDto dto, CandleResDto candle) {
        return SimulationOrder.builder()
                .simulationId(dto.getSimulationId())
                .amountOfTrade(dto.isMaxAmountOfTrade() ? simulation.getBalance() : dto.getAmountOfTrade())
                .openPrice(candle.getClose())
                .openTime(candle.getCloseTime())
                .status(SOrderStatus.OPEN)
                .type(dto.getOrderType())
                .uid(UUID.randomUUID().toString())
                .version(0)
                .extraInfo(null)
                .build();
    }
}
