package app.traderslave.command;

import app.traderslave.assembler.BackTestingServiceAssembler;
import app.traderslave.controller.dto.PostSimulationResDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.service.SimulationService;
import reactor.core.publisher.Mono;

public class CreateSimulationCommand extends BaseMonoCommand<CurrencyPair, PostSimulationResDto> {

    private final SimulationService simulationService;

    public CreateSimulationCommand(CurrencyPair requestDto, SimulationService simulationService) {
        super(requestDto);
        this.simulationService = simulationService;
    }

    @Override
    public Mono<PostSimulationResDto> execute() {
        Simulation simulation = simulationService.create(requestDto);
        return Mono.just(BackTestingServiceAssembler.toModel(simulation));
    }
}
