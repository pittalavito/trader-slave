package app.traderslave.adapter;

import app.traderslave.model.dto.SimulationPatterStrategyDto;
import app.traderslave.model.dto.req.CreateSimulationReqDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CreateSimulationDtoAdapter {

    public CreateSimulationReqDto adapt(SimulationPatterStrategyDto dto) {
        var createSimulation = new CreateSimulationReqDto();
        createSimulation.setCurrencyPair(dto.getCurrencyPair());
        createSimulation.setStartTime(dto.getSimulationStartTime());
        createSimulation.setDescription("Pattern Strategy Simulation");
        return createSimulation;
    }
}
