package app.traderslave.assembler;

import app.traderslave.controller.dto.CreateSimulationOrderResDto;
import app.traderslave.controller.dto.PostSimulationResDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

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

    public Mono<CreateSimulationOrderResDto> toModel(SimulationOrder simulationOrder, BigDecimal balance) {
        return Mono.just(
                CreateSimulationOrderResDto.builder()
                        .id(simulationOrder.getId())
                        .simulationId(simulationOrder.getSimulationId())
                        .orderType(simulationOrder.getType())
                        .amountOfTrade(simulationOrder.getAmountOfTrade())
                        .openPrice(simulationOrder.getOpenPrice())
                        .openTime(simulationOrder.getOpenTime())
                        .balance(balance)
                        .liquidationPrice(simulationOrder.getLiquidationPrice())
                        .leverage(simulationOrder.getLeverage())
                        .build()
        );
    }
}
