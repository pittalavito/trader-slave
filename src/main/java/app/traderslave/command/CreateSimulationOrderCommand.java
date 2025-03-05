package app.traderslave.command;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.assembler.TestingServiceAssembler;
import app.traderslave.checker.TestingChecker;
import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.controller.dto.SimulationOrderResDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.service.BinanceService;
import app.traderslave.service.SimulationEventService;
import app.traderslave.service.SimulationOrderService;
import app.traderslave.service.SimulationService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Scope("prototype")
public class CreateSimulationOrderCommand extends BaseMonoCommand<CreateSimulationOrderReqDto, SimulationOrderResDto> {

    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;
    private final SimulationEventService simulationEventService;

    protected CreateSimulationOrderCommand(CreateSimulationOrderReqDto requestDto, BinanceService binanceService, SimulationService simulationService, SimulationOrderService simulationOrderService, SimulationEventService simulationEventService) {
        this.requestDto = requestDto;
        this.binanceService = binanceService;
        this.simulationService = simulationService;
        this.simulationOrderService = simulationOrderService;
        this.simulationEventService = simulationEventService;
    }

    @Override
    public Mono<SimulationOrderResDto> execute() {
        //aggiungere controllo a leverage
        TestingChecker.checkAmountOfTrade(requestDto);
        Simulation simulation = simulationService.findByIdOrError(requestDto.getSimulationId());
        TestingChecker.checkBalance(simulation, requestDto);

        return binanceService.findCandle(BinanceServiceAdapter.adapt(simulation.getCurrencyPair(), requestDto))
                .map(candle -> createOrderAndUpdateBalance(candle, simulation));
    }

    private SimulationOrderResDto createOrderAndUpdateBalance(CandleResDto candle, Simulation simulation) {
        SimulationOrder order = simulationOrderService.create(simulation, requestDto, candle);
        Simulation updatedSimulation = simulationService.subtractBalance(simulation, order);
        simulationEventService.create(order);
        return TestingServiceAssembler.toModel(order, updatedSimulation.getBalance(), requestDto);
    }
}
