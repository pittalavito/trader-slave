package app.traderslave.command.simulation;

import app.traderslave.command.base.BaseCommand;
import app.traderslave.domain.service.SimulationDomainEventService;
import app.traderslave.domain.service.SimulationDomainService;
import app.traderslave.domain.service.SimulationOrderDomainService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteAllSimulationCommand extends BaseCommand<Void, Void> {

    private final SimulationDomainService simulationDomainService;
    private final SimulationOrderDomainService simulationOrderDomainService;
    private final SimulationDomainEventService simulationDomainEventService;

    @Override
    @Transactional
    public Void execute() {
        simulationDomainEventService.deleteAll();
        simulationOrderDomainService.deleteAll();
        simulationDomainService.deleteAll();
        return null;
    }
}
