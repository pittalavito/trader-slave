package app.traderslave.command;

import app.traderslave.assembler.TestingServiceAssembler;
import app.traderslave.controller.dto.CreateSimulationReqDto;
import app.traderslave.controller.dto.PostSimulationResDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.service.SimulationService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Scope("prototype")
public class CreateSimulationCommand extends BaseMonoCommand<CreateSimulationReqDto, PostSimulationResDto> {

    private final SimulationService simulationService;

    protected CreateSimulationCommand(CreateSimulationReqDto requestDto, SimulationService simulationService) {
        this.requestDto = requestDto;
        this.simulationService = simulationService;
    }

    @Override
    public Mono<PostSimulationResDto> execute() {
        Simulation simulation = simulationService.create(requestDto);
        return Mono.just(TestingServiceAssembler.toModel(simulation));
    }
}
