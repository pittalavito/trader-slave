package app.traderslave.service.simulation;

import app.traderslave.adapter.CandlesReqDtoAdapter;
import app.traderslave.assembler.SimulationServiceAssembler;
import app.traderslave.checker.SimulationServiceChecker;
import app.traderslave.checker.TimeChecker;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationService {

    private final SimulationRepository repository;
    private final BinanceService binanceService;
    private final SimulationOrderService simulationOrderService;
    private final SimulationEventService simulationEventService;
    private final SimulationOrderReportFactoryService simulationOrderReportFactoryService;

    public BigDecimal getBalance(Long simulationId) {
        Simulation simulation = findByIdOrError(simulationId);
        return simulation.getBalance();
    }

    /**
     * CREATE SIMULATION
     */
    public Mono<CreateSimulationResDto> create(CreateSimulationReqDto dto) {
        TimeChecker.checkStartDate(dto.getStartTime());
        Simulation simulation = repository.save(SimulationFactory.create(dto));
        return Mono.just(SimulationServiceAssembler.toModelCreate(simulation));
    }

    /**
     * CLOSE SIMULATION
     */
    public Mono<CloseSimulationResDto> close(CloseSimulationReqDto dto) {
        SimulationRecord data = validateReqAndGetRecord(dto);
        return execute(data, dto);
    }

    /**
     * CREATE ORDER
     */
    public Mono<SimulationOrderResDto> createOrder(CreateSimulationOrderReqDto dto) {
        SimulationRecord data = validateReqAndGetRecord(dto);
        return execute(data, dto);
    }

    /**
     * CLOSE ORDER
     */
    public Mono<SimulationOrderResDto> closeOrder(CloseSimulationOrderReqDto dto) {
        SimulationRecord data = validateReqAndGetRecord(dto);
        return execute(data, dto);
    }

    /**
     * CLEAN ALL DATA
     */
    @Transactional
    public void deleteAll() {
        simulationEventService.deleteAll();
        simulationOrderService.deleteAll();
        repository.deleteAll();
    }

    // VALIDATE REQ METHODS --------------------------------------------------------------------------------------------

    private SimulationRecord validateReqAndGetRecord(CreateSimulationOrderReqDto dto) {
        SimulationServiceChecker.checkLeverage(dto);
        SimulationServiceChecker.checkAmountOfTrade(dto);
        Simulation simulation = findByIdOrError(dto.getSimulationId());
        SimulationServiceChecker.checkSimulationStatusOpen(simulation);
        SimulationServiceChecker.checkBalance(simulation, dto);
        SimulationEvent latestEvent = simulationEventService.findLatestEventBySimulationId(simulation.getId());
        SimulationServiceChecker.checkRequestTime(simulation, latestEvent, dto);
        return new SimulationRecord(simulation, null);
    }

    private SimulationRecord validateReqAndGetRecord(CloseSimulationOrderReqDto dto) {
        Simulation simulation = findByIdOrError(dto.getSimulationId());
        SimulationServiceChecker.checkSimulationStatusOpen(simulation);
        SimulationEvent latestEvent = simulationEventService.findLatestEventBySimulationId(simulation.getId());
        SimulationServiceChecker.checkRequestTime(simulation, latestEvent, dto);
        SimulationOrder order = simulationOrderService.findByIdAndSimulationIdOrError(dto.getOrderId(), simulation.getId());
        SimulationServiceChecker.checkOrderStatusOpen(order);
        return new SimulationRecord(simulation, order);
    }

    private SimulationRecord validateReqAndGetRecord(CloseSimulationReqDto dto) {
        Simulation simulation = findByIdOrError(dto.getSimulationId());
        SimulationServiceChecker.checkSimulationStatusOpen(simulation);
        SimulationEvent latestEvent = simulationEventService.findLatestEventBySimulationId(simulation.getId());
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
        return simulationOrderReportFactoryService.create(simulation, order, dto)
                .map(report -> closeOrder(simulation, order, report, false))
                .map(updatedOrder -> SimulationServiceAssembler.toModelCloseOrder(updatedOrder, dto));
    }

    private Mono<CloseSimulationResDto> execute(SimulationRecord rec, CloseSimulationReqDto dto) {
        final Simulation simulation = rec.simulation();

        Map<Long, SimulationOrderResDto> ordersIdsMap = new HashMap<>();
        Map<SimulationOrderResDto.Status, List<Long>> ordersIdsStatusMap = new EnumMap<>(SimulationOrderResDto.Status.class);
        Arrays.stream(SimulationOrderResDto.Status.values()).forEach( status -> ordersIdsStatusMap.put(status, new ArrayList<>()));

        return Flux.fromIterable(simulationOrderService.findAllBySimulationId(simulation.getId()))
                .map(order -> closeStepOne(ordersIdsMap, ordersIdsStatusMap, order, dto))
                .filter(SimulationOrder::isOpen)
                .flatMap(order ->
                        simulationOrderReportFactoryService.create(simulation, order, dto)
                                .map(report -> closeStepTwo(ordersIdsMap, ordersIdsStatusMap, simulation, order, dto, report)))
                .collectList()
                .map(reports -> closeStepThree(simulation.getId(), ordersIdsMap, ordersIdsStatusMap, dto));
    }


    @Transactional
    private SimulationOrder createOrder(Simulation simulation, CreateSimulationOrderReqDto dto, CandleResDto candle) {
        SimulationOrder newOrder = simulationOrderService.create(simulation, dto, candle);
        repository.save(SimulationFactory.subtractBalance(simulation, newOrder));
        simulationEventService.create(newOrder, false);
        return newOrder;
    }

    @Transactional
    private SimulationOrder closeOrder(Simulation simulation, SimulationOrder order, OrderReport report, boolean endSimulation) {
        SimulationOrder closedOrder = simulationOrderService.close(order, report, endSimulation);
        repository.save(SimulationFactory.addBalance(simulation, order));
        simulationEventService.create(closedOrder, endSimulation);
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
        List<SimulationEvent> events = simulationEventService.findBySimulationIdOrderByEventTimeAsc(simulationId);
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
