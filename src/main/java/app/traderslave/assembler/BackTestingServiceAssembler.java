package app.traderslave.assembler;

import app.traderslave.controller.dto.PostSimulationResDto;
import app.traderslave.model.domain.Simulation;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class BackTestingServiceAssembler {

    public Mono<PostSimulationResDto> toModel(Simulation simulation) {
        return Mono.just(
                PostSimulationResDto.builder()
                        .simulationId(simulation.getId())
                        .balance(simulation.getBalance())
                        .currency(simulation.getCurrency())
                        .build()
        );
    }
}
