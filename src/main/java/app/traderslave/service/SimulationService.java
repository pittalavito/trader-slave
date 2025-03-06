package app.traderslave.service;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.assembler.SimulationServiceAssembler;
import app.traderslave.checker.SimulationServiceChecker;
import app.traderslave.controller.SimulationController;
import app.traderslave.controller.dto.*;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.factory.OrderReportFactory;
import app.traderslave.factory.SimulationFactory;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.SOrderStatus;
import app.traderslave.model.report.OrderReport;
import app.traderslave.repository.SimulationRepository;
import app.traderslave.model.domain.Simulation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationService {

    private final SimulationOrderReportService simulationOrderReportService;
    private final SimulationOrderService simulationOrderService;
    private final SimulationEventService simulationEventService;
    private final BinanceService binanceService;
    private final SimulationRepository repository;

    /**
     * For requests coming from {@link SimulationController#retrieve(GetSimulationReqDto)}
     */
    public Mono<GetSimulationResDto> retrieve(GetSimulationReqDto dto) {
        //todo
        return null;
    }

    /**
     * For requests coming from {@link SimulationController#create(CreateSimulationReqDto)}
     */
    @Transactional
    public Mono<PostSimulationResDto> create(CreateSimulationReqDto dto) {
        Simulation simulation = repository.save(SimulationFactory.create(dto));
        return Mono.just(SimulationServiceAssembler.toModel(simulation));
    }

    /**
     * For requests coming from {@link SimulationController#retrieveOrder(GetSimulationOrderReqDto)}
     */
    public Mono<SimulationOrderResDto> retrieveOrder(GetSimulationOrderReqDto dto) {
        Simulation simulation = findByIdOrError(dto.getSimulationId());
        SimulationOrder order = simulationOrderService.findByIdAndSimulationIdOrError(simulation.getId(), dto.getOrderId());

        if (SOrderStatus.OPEN != order.getStatus()) {
            OrderReport report = OrderReportFactory.create(order);
            return Mono.just(SimulationServiceAssembler.toModel(order, report, dto));
        }
        return simulationOrderReportService.create(order, dto)
                .map(report -> {
                    SimulationOrder orderResult = order;
                    if (report.isLiquidated()) {
                        orderResult = closeOrder(order, report);
                    }
                    return SimulationServiceAssembler.toModel(orderResult, report, dto);
                });
    }

    /**
     * For requests coming from {@link SimulationController#createOrder(CreateSimulationOrderReqDto)}
     */
    public Mono<SimulationOrderResDto> createOrder(CreateSimulationOrderReqDto requestDto) {
        SimulationServiceChecker.checkLeverage(requestDto);
        SimulationServiceChecker.checkAmountOfTrade(requestDto);
        Simulation simulation = findByIdOrError(requestDto.getSimulationId());
        SimulationServiceChecker.checkBalance(simulation, requestDto);

        return binanceService.findCandle(BinanceServiceAdapter.adapt(simulation.getCurrencyPair(), requestDto))
                .map(candle -> createOrder(simulation, requestDto, candle))
                .map(order -> SimulationServiceAssembler.toModel(order, requestDto));
    }

    /**
     * For requests coming from {@link SimulationController#closeOrder(CloseSimulationOrderReqDto)}
     */
    public Mono<SimulationOrderResDto> closeOrder(CloseSimulationOrderReqDto dto) {
        Simulation simulation = findByIdOrError(dto.getSimulationId());
        SimulationOrder order = simulationOrderService.findByIdAndSimulationIdOrError(simulation.getId(), dto.getOrderId());
        SimulationServiceChecker.checkOrderStatusOpen(order);

        return simulationOrderReportService.create(order, dto)
                .map(report -> {
                    SimulationOrder updatedOrder = closeOrder(order, report);
                    return SimulationServiceAssembler.toModel(updatedOrder, report, dto);
                });
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
}
