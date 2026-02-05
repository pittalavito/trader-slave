package app.traderslave.service.simulation;

import app.traderslave.command.simulation.CloseSimulationCommand;
import app.traderslave.command.simulation.CreateSimulationCommand;
import app.traderslave.command.simulation.DeleteAllSimulationCommand;
import app.traderslave.domain.service.SimulationDomainService;
import app.traderslave.model.dto.req.CloseSimulationReqDto;
import app.traderslave.model.dto.res.CloseSimulationResDto;
import app.traderslave.model.dto.req.CreateSimulationReqDto;
import app.traderslave.model.dto.res.CreateSimulationResDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationManagerService {

    private final SimulationDomainService simulationDomainService;
    private final CreateSimulationCommand createSimulationCommand;
    private final CloseSimulationCommand closeSimulationCommand;
    private final DeleteAllSimulationCommand deleteAllSimulationCommand;

    public BigDecimal getBalance(Long simulationId) {
        var simulation = simulationDomainService.findByIdOrError(simulationId);
        return simulation.getBalance();
    }

    public CreateSimulationResDto create(CreateSimulationReqDto dto) {
        createSimulationCommand.setCommandRequest(dto);
        return createSimulationCommand.execute();
    }

    @Transactional
    public CloseSimulationResDto close(CloseSimulationReqDto dto) {
        closeSimulationCommand.setCommandRequest(dto);
        return closeSimulationCommand.execute();
    }

    @Transactional
    public void deleteAll() {
        deleteAllSimulationCommand.execute();
    }
}
