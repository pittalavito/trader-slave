package app.traderslave.service;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.assembler.SimulationServiceAssembler;
import app.traderslave.checker.SimulationServiceChecker;
import app.traderslave.controller.SimulationController;
import app.traderslave.controller.dto.*;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.factory.SimulationFactory;
import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.report.OrderReport;
import app.traderslave.repository.SimulationRepository;
import app.traderslave.model.domain.Simulation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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

    public Mono<PostSimulationResDto> create(CreateSimulationReqDto dto) {
        Simulation simulation = repository.save(SimulationFactory.create(dto));
        return Mono.just(SimulationServiceAssembler.toModelCreate(simulation));
    }

    public Mono<SimulationOrderResDto> getOrder(GetSimulationOrderReqDto dto) {
        SimulationOrder order = findOrderWithSimulationField(dto.getSimulationId(), dto.getOrderId());

        if (SimulationOrder.Status.OPEN == order.getStatus()) {
            return simulationOrderReportFactoryService.create(order, dto)
                    .flatMap(report -> manageOpenOrderAndToModel(order, dto));
        }

        return Mono.just(SimulationServiceAssembler.toModelCloseOrder(order, dto));
    }

    public Mono<SimulationOrderResDto> createOrder(CreateSimulationOrderReqDto requestDto) {
        SimulationServiceChecker.checkLeverage(requestDto);
        SimulationServiceChecker.checkAmountOfTrade(requestDto);
        Simulation simulation = findByIdOrError(requestDto.getSimulationId());
        SimulationServiceChecker.checkBalance(simulation, requestDto);

        return binanceService.findCandle(BinanceServiceAdapter.adapt(simulation.getCurrencyPair(), requestDto))
                .map(candle -> createOrder(simulation, requestDto, candle))
                .map(order -> SimulationServiceAssembler.toModelCreateOrder(order, requestDto));
    }

    public Mono<SimulationOrderResDto> closeOrder(CloseSimulationOrderReqDto dto) {
        SimulationOrder order = findOrderWithSimulationField(dto.getSimulationId(), dto.getOrderId());
        SimulationServiceChecker.checkOrderStatusOpen(order);

        return simulationOrderReportFactoryService.create(order, dto)
                .map(report -> {
                    SimulationOrder updatedOrder = closeOrder(order, report);
                    return SimulationServiceAssembler.toModelCloseOrder(updatedOrder, dto);
                });
    }

    public Mono<CloseSimulationResDto> close(CloseSimulationReqDto dto) {
        Simulation simulation = findByIdOrError(dto.getSimulationId());

        Map<SimulationOrder.Status, List<SimulationOrderResDto>> ordersMap = new EnumMap<>(SimulationOrder.Status.class);
        ordersMap.put(SimulationOrder.Status.LIQUIDATED, new ArrayList<>());
        ordersMap.put(SimulationOrder.Status.CLOSED, new ArrayList<>());
        ordersMap.put(SimulationOrder.Status.OPEN, new ArrayList<>());

        List<SimulationOrder> openOrders = simulationOrderService.findAllBySimulationId(simulation.getId()).stream()
                .map(order -> addInfoInOrdersMapStepOne(ordersMap, simulation, order, dto))
                .filter(order -> SimulationOrder.Status.OPEN == order.getStatus())
                .toList();

        if (CollectionUtils.isEmpty(openOrders)) {
            List<SimulationEvent> events = simulationEventService.findBySimulationIdOrderByEventTimeAsc(simulation.getId());
            delete(dto);
            return Mono.just(SimulationServiceAssembler.toModelClose(ordersMap, events));
        }

        return Flux.fromIterable(openOrders)
                .flatMap(order -> addInfoInOrdersMapStepTwo(ordersMap, order, dto))
                .collectList()
                .flatMap(reports -> {
                    List<SimulationEvent> events = simulationEventService.findBySimulationIdOrderByEventTimeAsc(simulation.getId());
                    delete(dto);
                    return Mono.just(SimulationServiceAssembler.toModelClose(ordersMap, events));
                });
    }

    @Transactional
    public void deleteAll() {
        simulationEventService.deleteAll();
        simulationOrderService.deleteAll();
        repository.deleteAll();
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    @Transactional
    private SimulationOrder createOrder(Simulation simulation, CreateSimulationOrderReqDto dto, CandleResDto candle) {
        SimulationOrder newOrder = simulationOrderService.create(simulation, dto, candle);
        newOrder.setSimulation(subtractBalance(simulation, newOrder));
        simulationEventService.create(newOrder);
        return newOrder;
    }

    @Transactional
    private SimulationOrder closeOrder(SimulationOrder order, OrderReport report) {
        SimulationOrder closedOrder = simulationOrderService.close(order, report);
        closedOrder.setSimulation(addBalance(order.getSimulation(), closedOrder));
        simulationEventService.create(closedOrder);
        return closedOrder;
    }

    @Transactional
    private void delete(CloseSimulationReqDto dto) {
        if (dto.isDelete()) {
            delete(dto.getSimulationId());
        }
    }

    @Transactional
    public void delete(Long simulationId) {
        simulationEventService.deleteBySimulationId(simulationId);
        simulationOrderService.deleteBySimulationId(simulationId);
        repository.deleteById(simulationId);
    }

    private SimulationOrder findOrderWithSimulationField(Long simulationId, Long orderId) {
        Simulation simulation = findByIdOrError(simulationId);
        SimulationOrder order = simulationOrderService.findByIdAndSimulationIdOrError(orderId, simulationId);
        order.setSimulation(simulation);
        return order;
    }

    private Simulation findByIdOrError(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionEnum.SIMULATION_NOT_FOUND));
    }

    private Simulation subtractBalance(Simulation simulation, SimulationOrder order) {
        return repository.save(SimulationFactory.subtractBalance(simulation, order));
    }

    private Simulation addBalance(Simulation simulation, SimulationOrder order) {
        return repository.save(SimulationFactory.addBalance(simulation, order));
    }

    private Mono<SimulationOrderResDto> manageOpenOrderAndToModel(SimulationOrder order, TimeReqDto dto) {
        return simulationOrderReportFactoryService.create(order, dto)
                .map(report -> {
                    if (report.isLiquidated()) {
                        return closeOrderAndToModel(order, report, dto);
                    }
                    return SimulationServiceAssembler.toModelOpenOrder(order, report, dto);
                });
    }

    private SimulationOrder addInfoInOrdersMapStepOne(Map<SimulationOrder.Status, List<SimulationOrderResDto>> orders, Simulation simulation, SimulationOrder order, TimeReqDto dto) {
        final SimulationOrder.Status status = order.getStatus();
        if (SimulationOrder.Status.LIQUIDATED == status || SimulationOrder.Status.CLOSED == order.getStatus() ) {
            orders.get(status).add(SimulationServiceAssembler.toModelCloseOrder(order, dto));
        }
        order.setSimulation(simulation);
        return order;
    }

    private Mono<OrderReport> addInfoInOrdersMapStepTwo(Map<SimulationOrder.Status, List<SimulationOrderResDto>> orders, SimulationOrder order, TimeReqDto dto) {
        return simulationOrderReportFactoryService.create(order, dto)
                .map(report -> {
                    if (report.isLiquidated()) {
                        orders.get(SimulationOrder.Status.LIQUIDATED).add(closeOrderAndToModel(order, report, dto));
                    } else {
                        orders.get(SimulationOrder.Status.OPEN).add(SimulationServiceAssembler.toModelOpenOrder(order, report, dto));
                    }
                    return report;
                });
    }

    private SimulationOrderResDto closeOrderAndToModel(SimulationOrder order, OrderReport report, TimeReqDto dto) {
        SimulationOrder updatedOrder = closeOrder(order, report);
        return SimulationServiceAssembler.toModelCloseOrder(updatedOrder, dto);
    }
}
