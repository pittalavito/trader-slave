package app.traderslave.command;

import app.traderslave.assembler.TestingServiceAssembler;
import app.traderslave.controller.dto.CloseSimulationReqDto;
import app.traderslave.controller.dto.CloseSimulationResDto;
import app.traderslave.controller.dto.SimulationOrderResDto;
import app.traderslave.factory.ReportOrderFactory;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.SOrderStatus;
import app.traderslave.model.report.ReportOrder;
import app.traderslave.service.BinanceService;
import app.traderslave.service.SimulationEventService;
import app.traderslave.service.SimulationOrderService;
import app.traderslave.service.SimulationService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class CloseSimulationCommand extends BaseMonoCommand<CloseSimulationReqDto, CloseSimulationResDto> {

    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;
    private final SimulationEventService simulationEventService;
    private final List<SimulationOrderResDto> openOrders = new ArrayList<>();
    private final List<SimulationOrderResDto> closedOrders = new ArrayList<>();
    private final List<SimulationOrderResDto> liquidatedOrders = new ArrayList<>();

    protected CloseSimulationCommand(CloseSimulationReqDto dto, BinanceService binanceService, SimulationService simulationService, SimulationOrderService simulationOrderService, SimulationEventService simulationEventService) {
        this.requestDto = dto;
        this.binanceService = binanceService;
        this.simulationService = simulationService;
        this.simulationOrderService = simulationOrderService;
        this.simulationEventService = simulationEventService;
    }

    @Override
    public Mono<CloseSimulationResDto> execute() {
        Simulation simulation = simulationService.findByIdOrError(requestDto.getSimulationId());

        simulationOrderService.findBySimulationIdAndStatus(simulation.getId(), SOrderStatus.CLOSED)
                .forEach(order -> addNotOpenOrder(simulation, order, closedOrders));
        simulationOrderService.findBySimulationIdAndStatus(simulation.getId(), SOrderStatus.LIQUIDATED)
                .forEach(order -> addNotOpenOrder(simulation, order, liquidatedOrders));

        return Flux.fromIterable(simulationOrderService.findBySimulationIdAndStatus(simulation.getId(), SOrderStatus.OPEN))
                .flatMap(order -> createReportForOpenOrder(simulation, order))
                .then(Mono.defer(() -> toModel(simulation)));
    }

    private Mono<ReportOrder> createReportForOpenOrder(Simulation simulation, SimulationOrder order) {
        return binanceService.createReportOrder(simulation, order, requestDto)
                .doOnNext(report -> manageOpenOrder(simulation, order, report));
    }

    private void manageOpenOrder(Simulation simulation, SimulationOrder order, ReportOrder report) {
        if(report.isLiquidated()) {
            liquidateOrderAndUpdateBalance(simulation, order, report);
        } else {
            openOrders.add(TestingServiceAssembler.toModel(order, simulation, report, requestDto));
        }
    }

    private void liquidateOrderAndUpdateBalance(Simulation simulation, SimulationOrder order, ReportOrder report) {
        SimulationOrder updatedOrder = simulationOrderService.close(order, report);
        Simulation updatedSimulation = simulationService.addBalance(simulation, updatedOrder);
        simulationEventService.create(order);
        liquidatedOrders.add(TestingServiceAssembler.toModel(updatedOrder, updatedSimulation, report, requestDto));
    }

    private void addNotOpenOrder(Simulation simulation, SimulationOrder order, List<SimulationOrderResDto> notOpenOrders) {
        ReportOrder report = ReportOrderFactory.create(order);
        notOpenOrders.add(TestingServiceAssembler.toModel(order, simulation, report, requestDto));
    }

    private Mono<CloseSimulationResDto> toModel(Simulation simulation) {
        List<SimulationEvent> events = simulationEventService.findBySimulationIdOrderByEventTimeAsc(simulation.getId());
        //todo eliminare tutto dal db?
        return Mono.just(
                TestingServiceAssembler.toModel(simulation, events, openOrders, closedOrders, liquidatedOrders, requestDto
        ));
    }
}
