package app.traderslave.command.simulation;

import app.traderslave.checker.SimulationChecker;
import app.traderslave.command.base.BaseCommand;
import app.traderslave.controller.dto.*;
import app.traderslave.domain.model.Simulation;
import app.traderslave.domain.model.SimulationEvent;
import app.traderslave.domain.model.SimulationOrder;
import app.traderslave.domain.service.SimulationDomainEventService;
import app.traderslave.domain.service.SimulationDomainService;
import app.traderslave.domain.service.SimulationOrderDomainService;
import app.traderslave.model.OrderReport;
import app.traderslave.assembler.SimulationServiceAssembler;
import app.traderslave.service.simulation.SimulationOrderReportManagerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CloseSimulationCommand extends BaseCommand<CloseSimulationReqDto, CloseSimulationResDto> {

    private final SimulationDomainService simulationDomainService;
    private final SimulationOrderDomainService simulationOrderDomainService;
    private final SimulationDomainEventService simulationDomainEventService;
    private final SimulationOrderReportManagerService simulationOrderReportManagerService;

    @Override
    @Transactional
    public CloseSimulationResDto execute() {
        Simulation simulation = simulationDomainService.findByIdOrError(commandRequest.getSimulationId());
        SimulationChecker.checkSimulationStatusOpen(simulation);
        SimulationEvent latestEvent = simulationDomainEventService.findLatestEventBySimulationId(simulation.getId());
        SimulationChecker.checkRequestTime(simulation, latestEvent, commandRequest);

        Map<Long, SimulationOrderResDto> ordersIdsMap = new HashMap<>();
        Map<SimulationOrderResDto.Status, List<Long>> ordersIdsStatusMap = new EnumMap<>(SimulationOrderResDto.Status.class);
        Arrays.stream(SimulationOrderResDto.Status.values()).forEach(status -> ordersIdsStatusMap.put(status, new ArrayList<>()));

        List<SimulationOrder> orders = simulationOrderDomainService.findAllBySimulationId(simulation.getId());
        if (!CollectionUtils.isEmpty(orders)) {
            orders.forEach(order -> {
                SimulationOrderResDto resDto;
                if (order.isOpen()) {
                    OrderReport report = simulationOrderReportManagerService.createBackTestShortTermReport(simulation, order, commandRequest);
                    SimulationOrder closedOrder = simulationOrderDomainService.close(order, report, true);
                    simulationDomainService.subtractBalance(simulation, closedOrder);
                    simulationDomainEventService.create(closedOrder, true);
                    resDto = SimulationServiceAssembler.toModelCloseOrder(closedOrder, commandRequest);
                } else {
                    resDto = SimulationServiceAssembler.toModelCloseOrder(order, commandRequest);
                }
                ordersIdsMap.put(order.getId(), resDto);
                ordersIdsStatusMap.get(resDto.getStatus()).add(order.getId());
            });
        }

        List<SimulationEvent> events = simulationDomainEventService.findBySimulationIdOrderByEventTimeAsc(simulation.getId());
        Simulation closeSimulation = simulationDomainService.close(simulation, commandRequest);
        return SimulationServiceAssembler.toModelClose(closeSimulation, ordersIdsMap, ordersIdsStatusMap, events);
    }
}
