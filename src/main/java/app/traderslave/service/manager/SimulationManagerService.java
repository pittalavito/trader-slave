package app.traderslave.service.manager;

import app.traderslave.assembler.SimulationServiceAssembler;
import app.traderslave.checker.SimulationServiceChecker;
import app.traderslave.checker.TimeChecker;
import app.traderslave.controller.dto.*;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.report.OrderReport;
import app.traderslave.service.domain.SimulationDomainEventService;
import app.traderslave.service.domain.SimulationDomainService;
import app.traderslave.service.domain.SimulationOrderDomainService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationManagerService {

    private final SimulationDomainService simulationDomainService;
    private final SimulationOrderDomainService simulationOrderDomainService;
    private final SimulationDomainEventService simulationDomainEventService;
    private final SimulationOrderReportManagerService simulationOrderReportManagerService;

    public BigDecimal getBalance(Long simulationId) {
        Simulation simulation = simulationDomainService.findByIdOrError(simulationId);
        return simulation.getBalance();
    }

    public CreateSimulationResDto create(CreateSimulationReqDto dto) {
        TimeChecker.checkStartDate(dto.getStartTime());
        Simulation simulation = simulationDomainService.create(dto);
        return SimulationServiceAssembler.toModelCreate(simulation);
    }

    public CloseSimulationResDto close(CloseSimulationReqDto dto) {
        Simulation simulation = simulationDomainService.findByIdOrError(dto.getSimulationId());
        SimulationServiceChecker.checkSimulationStatusOpen(simulation);
        SimulationEvent latestEvent = simulationDomainEventService.findLatestEventBySimulationId(simulation.getId());
        SimulationServiceChecker.checkRequestTime(simulation, latestEvent, dto);

        Map<Long, SimulationOrderResDto> orderIdsMap = new HashMap<>();
        Map<SimulationOrderResDto.Status, List<Long>> ordersIdsStatusMap = new EnumMap<>(SimulationOrderResDto.Status.class);
        Arrays.stream(SimulationOrderResDto.Status.values()).forEach(status -> ordersIdsStatusMap.put(status, new ArrayList<>()));

        var result = simulationOrderDomainService.findAllBySimulationId(simulation.getId())
                .stream()
                .map(order -> {
                    if (!order.isOpen()) {
                        SimulationOrderResDto resDto = SimulationServiceAssembler.toModelCloseOrder(order, dto);
                        orderIdsMap.put(order.getId(), resDto);
                        ordersIdsStatusMap.get(resDto.getStatus()).add(order.getId());
                    }
                    return order;
                })
                .filter(SimulationOrder::isOpen)
                .map(order -> {
                    OrderReport report = simulationOrderReportManagerService.createBackTestShortTermReport(simulation, order, dto);
                    return closeStepTwo(orderIdsMap, ordersIdsStatusMap, simulation, order, dto, report);
                }).toList();

        return closeStepThree(simulation.getId(), orderIdsMap, ordersIdsStatusMap, dto);
    }

    @Transactional
    private SimulationOrderResDto closeStepTwo(Map<Long, SimulationOrderResDto> orders, Map<SimulationOrderResDto.Status, List<Long>> ordersIdsStatusMap, Simulation simulation, SimulationOrder order, TimeReqDto dto, OrderReport report) {
        SimulationOrder closedOrder = simulationOrderDomainService.close(order, report, false);
        simulationDomainService.subtractBalance(simulation, closedOrder);
        simulationDomainEventService.create(closedOrder, false);
        SimulationOrderResDto resDto = SimulationServiceAssembler.toModelCloseOrder(closedOrder, dto);
        orders.put(order.getId(), resDto);
        ordersIdsStatusMap.get(resDto.getStatus()).add(order.getId());
        return resDto;
    }

    @Transactional
    private CloseSimulationResDto closeStepThree(Long simulationId, Map<Long, SimulationOrderResDto> ordersIdsMap, Map<SimulationOrderResDto.Status, List<Long>> ordersIdsStatusMap, TimeReqDto dto) {
        List<SimulationEvent> events = simulationDomainEventService.findBySimulationIdOrderByEventTimeAsc(simulationId);
        Simulation simulation = simulationDomainService.findByIdOrError(simulationId);
        simulation = simulationDomainService.close(simulation, dto);
        return SimulationServiceAssembler.toModelClose(simulation, ordersIdsMap, ordersIdsStatusMap, events);
    }

    @Transactional
    public void deleteAll() {
        simulationDomainEventService.deleteAll();
        simulationOrderDomainService.deleteAll();
        simulationDomainService.deleteAll();
    }
}
