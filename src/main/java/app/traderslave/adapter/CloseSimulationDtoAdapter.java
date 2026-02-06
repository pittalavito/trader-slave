package app.traderslave.adapter;

import app.traderslave.model.dto.req.CloseSimulationReqDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class CloseSimulationDtoAdapter {

    public CloseSimulationReqDto adapt(Long simulationId, LocalDateTime localDateTime) {
        var closeSimulation = new CloseSimulationReqDto();
        closeSimulation.setSimulationId(simulationId);
        closeSimulation.setStartTime(localDateTime);
        return closeSimulation;
    }
}
