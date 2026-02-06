package app.traderslave.service.simulation;

import app.traderslave.command.simulation.CloseSimulationOrderCommand;
import app.traderslave.command.simulation.CreateSimulationOrderCommand;
import app.traderslave.model.dto.req.CloseSimulationOrderReqDto;
import app.traderslave.model.dto.req.CreateSimulationOrderReqDto;
import app.traderslave.model.dto.SimulationOrderDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationOrderManagerService {

    private final CreateSimulationOrderCommand createSimulationOrderCommand;
    private final CloseSimulationOrderCommand closeSimulationOrderCommand;

    public SimulationOrderDto create(CreateSimulationOrderReqDto dto) {
        createSimulationOrderCommand.setCommandRequest(dto);
        return createSimulationOrderCommand.execute();
    }

    @Transactional
    public SimulationOrderDto close(CloseSimulationOrderReqDto dto) {
        closeSimulationOrderCommand.setCommandRequest(dto);
        return closeSimulationOrderCommand.execute();
    }
}
