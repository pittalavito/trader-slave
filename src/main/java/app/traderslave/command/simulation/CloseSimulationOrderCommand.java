package app.traderslave.command.simulation;

import app.traderslave.checker.SimulationChecker;
import app.traderslave.command.base.BaseCommand;
import app.traderslave.model.dto.req.CandleReqDto;
import app.traderslave.model.dto.req.CloseSimulationOrderReqDto;
import app.traderslave.model.dto.req.CreateSimulationOrderReqDto;
import app.traderslave.model.dto.SimulationOrderDto;
import app.traderslave.domain.model.Simulation;
import app.traderslave.domain.model.SimulationEvent;
import app.traderslave.domain.model.SimulationOrder;
import app.traderslave.domain.service.SimulationDomainEventService;
import app.traderslave.domain.service.SimulationDomainService;
import app.traderslave.domain.service.SimulationOrderDomainService;
import app.traderslave.model.dto.OrderReportDto;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.assembler.SimulationServiceAssembler;
import app.traderslave.service.simulation.SimulationOrderReportManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CloseSimulationOrderCommand extends BaseCommand<CloseSimulationOrderReqDto, SimulationOrderDto> {

    private final SimulationDomainService simulationDomainService;
    private final SimulationOrderDomainService simulationOrderDomainService;
    private final SimulationDomainEventService simulationDomainEventService;
    private final SimulationOrderReportManagerService simulationOrderReportManagerService;


    @Override
    public SimulationOrderDto execute() {
        Simulation simulation = simulationDomainService.findByIdOrError(commandRequest.getSimulationId());
        SimulationChecker.checkSimulationStatusOpen(simulation);
        SimulationEvent latestEvent = simulationDomainEventService.findLatestEventBySimulationId(simulation.getId());
        SimulationChecker.checkRequestTime(simulation, latestEvent, commandRequest);
        SimulationOrder order = simulationOrderDomainService.findByIdAndSimulationIdOrError(commandRequest.getOrderId(), simulation.getId());
        SimulationChecker.checkOrderStatusOpen(order);
        OrderReportDto report = simulationOrderReportManagerService.createBackTestShortTermReport(simulation, order, commandRequest);
        SimulationOrder closedOrder = simulationOrderDomainService.close(order, report, false);
        simulationDomainService.addBalance(simulation, closedOrder);
        simulationDomainEventService.create(closedOrder, false);
        return SimulationServiceAssembler.toModelCloseOrder(closedOrder, commandRequest);
    }

    public CandleReqDto adapt(CurrencyPair currencyPair, CreateSimulationOrderReqDto dto) {
        CandleReqDto reqDto = new CandleReqDto();
        reqDto.setCurrencyPair(currencyPair);
        reqDto.setStartTime(dto.getStartTime());
        reqDto.setRealTimeRequest(dto.isRealTimeRequest());
        return reqDto;
    }
}
