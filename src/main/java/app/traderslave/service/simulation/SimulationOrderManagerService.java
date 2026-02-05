package app.traderslave.service.simulation;

import app.traderslave.command.simulation.CloseSimulationOrderCommand;
import app.traderslave.command.simulation.CreateSimulationOrderCommand;
import app.traderslave.controller.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationOrderManagerService {

    private final CreateSimulationOrderCommand createSimulationOrderCommand;
    private final CloseSimulationOrderCommand closeSimulationOrderCommand;

    public SimulationOrderResDto create(CreateSimulationOrderReqDto dto) {
        createSimulationOrderCommand.setCommandRequest(dto);
        return createSimulationOrderCommand.execute();
    }

    @Transactional
    public SimulationOrderResDto close(CloseSimulationOrderReqDto dto) {
        closeSimulationOrderCommand.setCommandRequest(dto);
        return closeSimulationOrderCommand.execute();
    }
}
