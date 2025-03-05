package app.traderslave.command;

import app.traderslave.assembler.TestingServiceAssembler;
import app.traderslave.controller.dto.*;
import app.traderslave.factory.ReportOrderFactory;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.SOrderStatus;
import app.traderslave.model.report.ReportOrder;
import app.traderslave.service.BinanceService;
import app.traderslave.service.SimulationOrderService;
import app.traderslave.service.SimulationService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Scope("prototype")
public class GetSimulationOrderCommand extends BaseMonoCommand<GetSimulationOrderReqDto, SimulationOrderResDto> {

    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;

    protected GetSimulationOrderCommand(GetSimulationOrderReqDto requestDto, BinanceService binanceService, SimulationService simulationService, SimulationOrderService simulationOrderService) {
        this.requestDto = requestDto;
        this.binanceService = binanceService;
        this.simulationService = simulationService;
        this.simulationOrderService = simulationOrderService;
    }

    @Override
    public Mono<SimulationOrderResDto> execute() {
        Simulation simulation = simulationService.findByIdOrError(requestDto.getSimulationId());
        SimulationOrder order = simulationOrderService.findByIdAndSimulationIdOrError(requestDto.getOrderId(), requestDto.getSimulationId());

        if (SOrderStatus.OPEN != order.getStatus()) {
            return responseNotOpenOrder(simulation, order);
        }

        return binanceService.createReportOrder(simulation, order, requestDto)
                .map(reportOrder -> responseOpenOrder(simulation, order, reportOrder));
    }

    private Mono<SimulationOrderResDto> responseNotOpenOrder(Simulation simulation, SimulationOrder order) {
        ReportOrder report = ReportOrderFactory.create(order);
        return Mono.just(TestingServiceAssembler.toModel(order, simulation, report, requestDto));
    }

    private SimulationOrderResDto responseOpenOrder(Simulation simulation, SimulationOrder order, ReportOrder report) {
        return report.isLiquidated() ?
                liquidateOrderAndUpdateBalance(simulation, order, report) :
                TestingServiceAssembler.toModel(order, simulation, report, requestDto);
    }

    private SimulationOrderResDto liquidateOrderAndUpdateBalance(Simulation simulation, SimulationOrder order, ReportOrder report) {
        SimulationOrder updatedOrder = simulationOrderService.close(order, report);
        Simulation updatedSimulation = simulationService.addBalance(simulation, updatedOrder);
        return TestingServiceAssembler.toModel(updatedOrder, updatedSimulation, report, requestDto);
    }

}
