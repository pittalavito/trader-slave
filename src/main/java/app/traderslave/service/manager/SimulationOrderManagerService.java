package app.traderslave.service.manager;

import app.traderslave.adapter.CandlesReqDtoAdapter;
import app.traderslave.assembler.CandlesResDtoAssembler;
import app.traderslave.assembler.SimulationServiceAssembler;
import app.traderslave.checker.SimulationServiceChecker;
import app.traderslave.controller.dto.*;
import app.traderslave.model.domain.CandleBackTest;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.report.OrderReport;
import app.traderslave.service.domain.CandleBackTestDomainService;
import app.traderslave.service.domain.SimulationDomainEventService;
import app.traderslave.service.domain.SimulationDomainService;
import app.traderslave.service.domain.SimulationOrderDomainService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationOrderManagerService {

    private final SimulationDomainService simulationDomainService;
    private final SimulationOrderDomainService simulationOrderDomainService;
    private final SimulationDomainEventService simulationDomainEventService;
    private final CandleBackTestDomainService candleBackTestDomainService;
    private final SimulationOrderReportManagerService simulationOrderReportManagerService;

    public SimulationOrderResDto create(CreateSimulationOrderReqDto dto) {
        SimulationServiceChecker.checkLeverage(dto);
        SimulationServiceChecker.checkAmountOfTrade(dto);
        Simulation simulation = simulationDomainService.findByIdOrError(dto.getSimulationId());
        SimulationServiceChecker.checkSimulationStatusOpen(simulation);
        SimulationServiceChecker.checkBalance(simulation, dto);
        SimulationEvent latestEvent = simulationDomainEventService.findLatestEventBySimulationId(simulation.getId());
        SimulationServiceChecker.checkRequestTime(simulation, latestEvent, dto);

        CandleReqDto request = CandlesReqDtoAdapter.adapt(simulation.getCurrencyPair(), dto);
        CandleBackTest candleBackTest = candleBackTestDomainService.getCandle(request);
        CandleResDto candle = CandlesResDtoAssembler.toModel(candleBackTest);
        SimulationOrder newOrder = simulationOrderDomainService.create(simulation, dto, candle);
        simulationDomainService.subtractBalance(simulation, newOrder);
        simulationDomainEventService.create(newOrder, false);
        return SimulationServiceAssembler.toModelCreateOrder(newOrder, dto);
    }

    @Transactional
    public SimulationOrderResDto close(CloseSimulationOrderReqDto dto) {
        Simulation simulation = simulationDomainService.findByIdOrError(dto.getSimulationId());
        SimulationServiceChecker.checkSimulationStatusOpen(simulation);
        SimulationEvent latestEvent = simulationDomainEventService.findLatestEventBySimulationId(simulation.getId());
        SimulationServiceChecker.checkRequestTime(simulation, latestEvent, dto);
        SimulationOrder order = simulationOrderDomainService.findByIdAndSimulationIdOrError(dto.getOrderId(), simulation.getId());
        SimulationServiceChecker.checkOrderStatusOpen(order);

        OrderReport report = simulationOrderReportManagerService.createBackTestShortTermReport(simulation, order, dto);
        SimulationOrder closedOrder = simulationOrderDomainService.close(order, report, false);
        simulationDomainService.addBalance(simulation, closedOrder);
        simulationDomainEventService.create(closedOrder, false);
        return SimulationServiceAssembler.toModelCloseOrder(closedOrder, dto);
    }
}
