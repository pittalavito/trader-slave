package app.traderslave.command;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.assembler.TestingServiceAssembler;
import app.traderslave.checker.TestingChecker;
import app.traderslave.controller.dto.*;
import app.traderslave.factory.ReportOrderFactory;
import app.traderslave.model.report.ReportOrder;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.service.BinanceService;
import app.traderslave.service.SimulationOrderService;
import app.traderslave.service.SimulationService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Scope("prototype")
public class CloseSimulationOrderCommand extends BaseMonoCommand<CloseSimulationOrderReqDto, SimulationOrderResDto> {

    private final Long orderId;
    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;

    protected CloseSimulationOrderCommand(CloseSimulationOrderReqDto requestDto, Long orderId, BinanceService binanceService, SimulationService simulationService, SimulationOrderService simulationOrderService) {
        this.requestDto = requestDto;
        this.orderId = orderId;
        this.binanceService = binanceService;
        this.simulationService = simulationService;
        this.simulationOrderService = simulationOrderService;
    }

    @Override
    public Mono<SimulationOrderResDto> execute() {
        Simulation simulation = simulationService.findByIdOrError(requestDto.getSimulationId());
        SimulationOrder order = simulationOrderService.findByIdAndSimulationIdOrError(orderId, requestDto.getSimulationId());
        TestingChecker.checkOrderStatusOpen(order);

        CandlesReqDto candleReqDto = BinanceServiceAdapter.adapt(order, simulation, requestDto);

        return binanceService.findCandles(candleReqDto)
                .map(candles -> closeOrderAndUpdateBalance(order, candles, simulation));
    }

    private SimulationOrderResDto closeOrderAndUpdateBalance(SimulationOrder order, CandlesResDto candles, Simulation simulation) {
        ReportOrder report = ReportOrderFactory.create(order, candles);
        SimulationOrder updatedOrder = simulationOrderService.close(order, report);
        Simulation updatedSimulation = simulationService.addBalance(simulation, updatedOrder);
        return TestingServiceAssembler.toModel(updatedOrder, updatedSimulation, report);
    }

}
