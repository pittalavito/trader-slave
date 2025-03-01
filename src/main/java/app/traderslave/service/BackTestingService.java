package app.traderslave.service;

import app.traderslave.command.CloseOrderSimulationCommand;
import app.traderslave.command.CreateSimulationCommand;
import app.traderslave.command.CreateSimulationOrderCommand;
import app.traderslave.controller.dto.*;
import app.traderslave.model.enums.CurrencyPair;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestingService {

    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;

    @Transactional
    public Mono<PostSimulationResDto> createSimulation(CurrencyPair currencyPair) {
        CreateSimulationCommand command = new CreateSimulationCommand(currencyPair, simulationService);
        return command.execute();
    }

    @Transactional
    public Mono<SimulationOrderResDto> createOrder(CreateSimulationOrderReqDto reqDto) {
        CreateSimulationOrderCommand command = new CreateSimulationOrderCommand(reqDto, binanceService, simulationService, simulationOrderService);
        return command.execute();
    }

    @Transactional
    public Mono<CloseSimulationOrderResDto> closeOrder(Long orderId, CloseSimulationOrderReqDto reqDto) {
        CloseOrderSimulationCommand command = new CloseOrderSimulationCommand(reqDto, orderId, binanceService, simulationService, simulationOrderService);
        return command.execute();
    }


    public Mono<Object> closeSimulation(Long id, boolean delete) {
        //todo implement
        return null;
    }

    //@Transactional
    //public Mono<GetSimulationResDto> getSimulationStatus(Long simulationId, LocalDateTime time) {
    //    AtomicReference<Simulation> simulation = new AtomicReference<>(simulationService.findByIdOrError(simulationId));
    //    List<SimulationOrder> openOrders = simulationOrderService.findBySimulationIdAndStatus(simulationId, SOrderStatus.OPEN);
    //    if (!CollectionUtils.isEmpty(openOrders)) {
    //        openOrders.forEach(order -> manageOpenOrder(openOrders, order, simulation, time));
    //    }
    //    List<SimulationOrder> closedOrders = simulationOrderService.findBySimulationIdAndStatus(simulationId, SOrderStatus.CLOSE);
    //    List<SimulationOrder> liquidatedOrders = simulationOrderService.findBySimulationIdAndStatus(simulationId, SOrderStatus.LIQUIDATED);
    //    return BackTestingServiceAssembler.toModel(simulation.get(), openOrders, closedOrders, liquidatedOrders);
    //}
    //
    //public void manageOpenOrder(List<SimulationOrder> openOrders, SimulationOrder order, AtomicReference<Simulation> simulation, LocalDateTime time)  {
    //
    //    CandlesReqDto candlesReqDto = BinanceServiceAdapter.adapt(order, simulation.get(), time);
    //
    //    binanceService.findCandles(candlesReqDto).subscribe(candles -> {
    //        ReportOrder report = ReportOrderFactory.create(order, candles);
    //
    //        if (report.isLiquidated()) {
    //            openOrders.remove(order);
    //            simulationOrderService.liquidate(order, report);
    //            simulation.set(simulationService.updateBalance(simulation.get(), order));
    //        } else {
    //            order.setReport(report);
    //        }
    //    });
    //}
}
