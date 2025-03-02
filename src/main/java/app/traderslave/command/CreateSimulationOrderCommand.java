package app.traderslave.command;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.assembler.BackTestingServiceAssembler;
import app.traderslave.checker.BackTestingChecker;
import app.traderslave.controller.dto.CandleReqDto;
import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.controller.dto.SimulationOrderResDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.service.BinanceService;
import app.traderslave.service.SimulationOrderService;
import app.traderslave.service.SimulationService;
import reactor.core.publisher.Mono;

public class CreateSimulationOrderCommand extends BaseMonoCommand<CreateSimulationOrderReqDto, SimulationOrderResDto> {

    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;

    public CreateSimulationOrderCommand(CreateSimulationOrderReqDto requestDto, BinanceService binanceService, SimulationService simulationService, SimulationOrderService simulationOrderService) {
        super(requestDto);
        this.binanceService = binanceService;
        this.simulationService = simulationService;
        this.simulationOrderService = simulationOrderService;
    }

    @Override
    public Mono<SimulationOrderResDto> execute() {
        BackTestingChecker.checkAmountOfTrade(requestDto);
        Simulation simulation = simulationService.findByIdOrError(requestDto.getSimulationId());
        BackTestingChecker.checkBalance(simulation, requestDto);

        CandleReqDto candleReqDto = BinanceServiceAdapter.adapt(simulation.getCurrencyPair(), requestDto);

        return binanceService.findCandle(candleReqDto)
                .map(candle -> createOrderAndUpdateBalance(candle, simulation));
    }

    private SimulationOrderResDto createOrderAndUpdateBalance(CandleResDto candle, Simulation simulation) {
        SimulationOrder order = simulationOrderService.create(simulation, requestDto, candle);
        Simulation updatedSimulation = simulationService.subtractBalance(simulation, order);
        return BackTestingServiceAssembler.toModel(order, updatedSimulation.getBalance());
    }
}
