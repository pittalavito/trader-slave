package app.traderslave.service.old;

import app.traderslave.adapter.CandlesReqDtoAdapter;
import app.traderslave.assembler.SimulationServiceAssembler;
import app.traderslave.checker.SimulationServiceChecker;
import app.traderslave.controller.dto.*;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.factory.SimulationFactory;
import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.report.OrderReport;
import app.traderslave.repository.SimulationRepository;
import app.traderslave.model.domain.Simulation;
import app.traderslave.service.BinanceService;
import app.traderslave.service.domain.SimulationDomainEventService;
import app.traderslave.service.domain.SimulationOrderDomainService;
import app.traderslave.service.manager.SimulationOrderReportManagerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationService {

    private final SimulationRepository repository;
    private final BinanceService binanceService;
    private final SimulationOrderDomainService simulationOrderDomainService;
    private final SimulationDomainEventService simulationDomainEventService;
    private final SimulationOrderReportManagerService simulationOrderReportManagerService;

    /**
     * CLOSE SIMULATION
     */
    public Mono<CloseSimulationResDto> close(CloseSimulationReqDto dto) {
        SimulationRecord data = validateReqAndGetRecord(dto);
        return execute(data, dto);
    }

    /**
     * CLEAN ALL DATA
     */
    @Transactional
    public void deleteAll() {
        simulationDomainEventService.deleteAll();
        simulationOrderDomainService.deleteAll();
        repository.deleteAll();
    }

    // VALIDATE REQ METHODS --------------------------------------------------------------------------------------------

    private SimulationRecord validateReqAndGetRecord(CreateSimulationOrderReqDto dto) {
        SimulationServiceChecker.checkLeverage(dto);
        SimulationServiceChecker.checkAmountOfTrade(dto);
        Simulation simulation = findByIdOrError(dto.getSimulationId());
        SimulationServiceChecker.checkSimulationStatusOpen(simulation);
        SimulationServiceChecker.checkBalance(simulation, dto);
        SimulationEvent latestEvent = simulationDomainEventService.findLatestEventBySimulationId(simulation.getId());
        SimulationServiceChecker.checkRequestTime(simulation, latestEvent, dto);
        return new SimulationRecord(simulation, null);
    }

    private SimulationRecord validateReqAndGetRecord(CloseSimulationOrderReqDto dto) {
        Simulation simulation = findByIdOrError(dto.getSimulationId());
        SimulationServiceChecker.checkSimulationStatusOpen(simulation);
        SimulationEvent latestEvent = simulationDomainEventService.findLatestEventBySimulationId(simulation.getId());
        SimulationServiceChecker.checkRequestTime(simulation, latestEvent, dto);
        SimulationOrder order = simulationOrderDomainService.findByIdAndSimulationIdOrError(dto.getOrderId(), simulation.getId());
        SimulationServiceChecker.checkOrderStatusOpen(order);
        return new SimulationRecord(simulation, order);
    }

    private SimulationRecord validateReqAndGetRecord(CloseSimulationReqDto dto) {
        Simulation simulation = findByIdOrError(dto.getSimulationId());
        SimulationServiceChecker.checkSimulationStatusOpen(simulation);
        SimulationEvent latestEvent = simulationDomainEventService.findLatestEventBySimulationId(simulation.getId());
        SimulationServiceChecker.checkRequestTime(simulation, latestEvent, dto);
        return new SimulationRecord(simulation, null);
    }

    // EXECUTE LOGIC METHODS -------------------------------------------------------------------------------------------

    private Mono<SimulationOrderResDto> execute(SimulationRecord rec, CreateSimulationOrderReqDto dto) {
        final Simulation simulation = rec.simulation();
        CandleReqDto request = CandlesReqDtoAdapter.adapt(simulation.getCurrencyPair(), dto);
        return binanceService.findCandle(request)
                .map(candle -> createOrder(simulation, dto, candle))
                .map(order -> SimulationServiceAssembler.toModelCreateOrder(order, dto));
    }

    private Mono<SimulationOrderResDto> execute(SimulationRecord rec, CloseSimulationOrderReqDto dto) {
        final Simulation simulation = rec.simulation();
        final SimulationOrder order = rec.order();
        return simulationOrderReportManagerService.createByBinanceApi(simulation, order, dto)
                .map(report -> closeOrder(simulation, order, report, false))
                .map(updatedOrder -> SimulationServiceAssembler.toModelCloseOrder(updatedOrder, dto));
    }

    private Mono<CloseSimulationResDto> execute(SimulationRecord rec, CloseSimulationReqDto dto) {
        final Simulation simulation = rec.simulation();

        Map<Long, SimulationOrderResDto> ordersIdsMap = new HashMap<>();
        Map<SimulationOrderResDto.Status, List<Long>> ordersIdsStatusMap = new EnumMap<>(SimulationOrderResDto.Status.class);
        Arrays.stream(SimulationOrderResDto.Status.values()).forEach( status -> ordersIdsStatusMap.put(status, new ArrayList<>()));

        return Flux.fromIterable(simulationOrderDomainService.findAllBySimulationId(simulation.getId()))
                .map(order -> closeStepOne(ordersIdsMap, ordersIdsStatusMap, order, dto))
                .filter(SimulationOrder::isOpen)
                .flatMap(order ->
                        simulationOrderReportManagerService.createByBinanceApi(simulation, order, dto)
                                .map(report -> closeStepTwo(ordersIdsMap, ordersIdsStatusMap, simulation, order, dto, report)))
                .collectList()
                .map(reports -> closeStepThree(simulation.getId(), ordersIdsMap, ordersIdsStatusMap, dto));
    }


    @Transactional
    private SimulationOrder createOrder(Simulation simulation, CreateSimulationOrderReqDto dto, CandleResDto candle) {
        SimulationOrder newOrder = simulationOrderDomainService.create(simulation, dto, candle);
        repository.save(SimulationFactory.subtractBalance(simulation, newOrder));
        simulationDomainEventService.create(newOrder, false);
        return newOrder;
    }

    @Transactional
    private SimulationOrder closeOrder(Simulation simulation, SimulationOrder order, OrderReport report, boolean endSimulation) {
        SimulationOrder closedOrder = simulationOrderDomainService.close(order, report, endSimulation);
        repository.save(SimulationFactory.addBalance(simulation, order));
        simulationDomainEventService.create(closedOrder, endSimulation);
        return closedOrder;
    }

    private Simulation findByIdOrError(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionEnum.SIMULATION_NOT_FOUND));
    }

    private SimulationOrder closeStepOne(Map<Long, SimulationOrderResDto> orderIdsMap, Map<SimulationOrderResDto.Status, List<Long>> ordersIdsStatusMap, SimulationOrder order, TimeReqDto dto) {
        if (!order.isOpen()) {
            SimulationOrderResDto resDto = SimulationServiceAssembler.toModelCloseOrder(order, dto);
            orderIdsMap.put(order.getId(), resDto);
            ordersIdsStatusMap.get(resDto.getStatus()).add(order.getId());
        }
        return order;
    }

    private SimulationOrderResDto closeStepTwo(Map<Long, SimulationOrderResDto> orders, Map<SimulationOrderResDto.Status, List<Long>> ordersIdsStatusMap, Simulation simulation, SimulationOrder order, TimeReqDto dto, OrderReport report) {
        SimulationOrder updatedOrder = closeOrder(simulation, order, report, true);
        SimulationOrderResDto resDto = SimulationServiceAssembler.toModelCloseOrder(updatedOrder, dto);
        orders.put(order.getId(), resDto);
        ordersIdsStatusMap.get(resDto.getStatus()).add(order.getId());
        return resDto;
    }

    @Transactional
    private CloseSimulationResDto closeStepThree(Long simulationId, Map<Long, SimulationOrderResDto> ordersIdsMap, Map<SimulationOrderResDto.Status, List<Long>> ordersIdsStatusMap, TimeReqDto dto) {
        List<SimulationEvent> events = simulationDomainEventService.findBySimulationIdOrderByEventTimeAsc(simulationId);
        Simulation simulation = findByIdOrError(simulationId);
        simulation = repository.save(SimulationFactory.close(simulation, dto));
        CloseSimulationResDto resDto = SimulationServiceAssembler.toModelClose(simulation, ordersIdsMap, ordersIdsStatusMap, events);
        //simulationEventService.deleteAll();
        //simulationOrderService.deleteAll();
        return resDto;
    }

    private record SimulationRecord(Simulation simulation, SimulationOrder order) {

    }
}
