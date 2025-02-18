package app.traderslave.assembler;

import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.controller.dto.PostSimulationResDto;
import app.traderslave.model.domain.Simulation;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class SimulationServiceAssembler {

    public Mono<PostSimulationResDto> toModel(Mono<CandlesResDto> candlesResDto, Simulation simulation) {
        return candlesResDto.map(candles ->
                PostSimulationResDto.builder()
                        .candles(null)
                        .simulationId(simulation.getId())
                        .balance(simulation.getBalance())
                        .currency(simulation.getCurrency())
                        .candles(candles)
                        .timeFrame(simulation.getTimeFrame())
                        .build()
        );
    }
}
