package app.traderslave.command.simulation;

import app.traderslave.checker.TimeChecker;
import app.traderslave.command.base.BaseCommand;
import app.traderslave.model.dto.req.CreateSimulationReqDto;
import app.traderslave.model.dto.CreateSimulationDto;
import app.traderslave.domain.model.Simulation;
import app.traderslave.domain.service.SimulationDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSimulationCommand extends BaseCommand<CreateSimulationReqDto, CreateSimulationDto> {

    private final SimulationDomainService simulationDomainService;

    @Override
    public CreateSimulationDto execute() {
        TimeChecker.checkStartDate(commandRequest.getStartTime());
        Simulation simulation = simulationDomainService.create(commandRequest);
        return toModel(simulation);
    }

    public CreateSimulationDto toModel(Simulation simulation) {
        return CreateSimulationDto.builder()
                .id(simulation.getId())
                .currencyPair(simulation.getCurrencyPair())
                .description(simulation.getDescription())
                .balance(simulation.getBalance())
                .currency(simulation.getCurrency())
                .build();
    }
}
