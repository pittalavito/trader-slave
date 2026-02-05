package app.traderslave.command.simulation;

import app.traderslave.checker.TimeChecker;
import app.traderslave.command.base.BaseCommand;
import app.traderslave.controller.dto.CreateSimulationReqDto;
import app.traderslave.controller.dto.CreateSimulationResDto;
import app.traderslave.domain.model.Simulation;
import app.traderslave.domain.service.SimulationDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSimulationCommand extends BaseCommand<CreateSimulationReqDto, CreateSimulationResDto> {

    private final SimulationDomainService simulationDomainService;

    @Override
    public CreateSimulationResDto execute() {
        TimeChecker.checkStartDate(commandRequest.getStartTime());
        Simulation simulation = simulationDomainService.create(commandRequest);
        return toModel(simulation);
    }

    public CreateSimulationResDto toModel(Simulation simulation) {
        return CreateSimulationResDto.builder()
                .id(simulation.getId())
                .currencyPair(simulation.getCurrencyPair())
                .description(simulation.getDescription())
                .balance(simulation.getBalance())
                .currency(simulation.getCurrency())
                .build();
    }
}
