package app.traderslave.command;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.assembler.BackTestingServiceAssembler;
import app.traderslave.checker.BackTestingChecker;
import app.traderslave.checker.TimeChecker;
import app.traderslave.controller.dto.*;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.factory.ReportOrderFactory;
import app.traderslave.model.domain.ReportOrder;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.SOrderStatus;
import app.traderslave.service.BinanceService;
import app.traderslave.service.SimulationOrderService;
import app.traderslave.service.SimulationService;
import reactor.core.publisher.Mono;

public class CloseOrderSimulationCommand extends BaseMonoCommand<CloseSimulationOrderReqDto, SimulationOrderResDto> {

    private final Long orderId;
    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;

    public CloseOrderSimulationCommand(CloseSimulationOrderReqDto requestDto, Long orderId, BinanceService binanceService, SimulationService simulationService, SimulationOrderService simulationOrderService) {
        super(requestDto);
        this.orderId = orderId;
        this.binanceService = binanceService;
        this.simulationService = simulationService;
        this.simulationOrderService = simulationOrderService;
    }

    @Override
    public Mono<SimulationOrderResDto> execute() {
        Simulation simulation = simulationService.findByIdOrError(requestDto.getSimulationId());
        SimulationOrder order = simulationOrderService.findByIdAndSimulationIdOrError(orderId, requestDto.getSimulationId());
        BackTestingChecker.checkOrderStatusOpen(order);

        CandlesReqDto candleReqDto = BinanceServiceAdapter.adapt(order, simulation, requestDto);

        return binanceService.findCandles(candleReqDto)
                .map(candles -> closeOrderAndUpdateBalance(order, candles, simulation));
    }

    private SimulationOrderResDto closeOrderAndUpdateBalance(SimulationOrder order, CandlesResDto candles, Simulation simulation) {
        ReportOrder report = ReportOrderFactory.create(order, candles);
        SimulationOrder updateOrder = simulationOrderService.close(order, report);
        Simulation updatedSimulation = simulationService.addBalance(simulation, updateOrder);
        return BackTestingServiceAssembler.toModel(updateOrder, updatedSimulation);
    }

}
