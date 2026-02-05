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
import app.traderslave.model.Candle;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.remote.service.BinanceRemoteService;
import app.traderslave.assembler.SimulationServiceAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSimulationOrderCommand extends BaseCommand<CreateSimulationOrderReqDto, SimulationOrderResDto> {

    private final SimulationDomainService simulationDomainService;
    private final SimulationOrderDomainService simulationOrderDomainService;
    private final SimulationDomainEventService simulationDomainEventService;
    private final BinanceRemoteService binanceRemoteService;

    @Override
    public SimulationOrderResDto execute() {
        SimulationChecker.checkLeverage(commandRequest);
        SimulationChecker.checkAmountOfTrade(commandRequest);
        Simulation simulation = simulationDomainService.findByIdOrError(commandRequest.getSimulationId());
        SimulationChecker.checkSimulationStatusOpen(simulation);
        SimulationChecker.checkBalance(simulation, commandRequest);
        SimulationEvent latestEvent = simulationDomainEventService.findLatestEventBySimulationId(simulation.getId());
        SimulationChecker.checkRequestTime(simulation, latestEvent, commandRequest);
        CandleReqDto candleRequest = adapt(simulation.getCurrencyPair(), commandRequest);
        Candle candleResponse = binanceRemoteService.findCandleSync(candleRequest);
        SimulationOrder newOrder = simulationOrderDomainService.create(simulation, commandRequest, candleResponse);
        simulationDomainService.subtractBalance(simulation, newOrder);
        simulationDomainEventService.create(newOrder, false);
        return SimulationServiceAssembler.toModelCreateOrder(newOrder, commandRequest);
    }

    public CandleReqDto adapt(CurrencyPair currencyPair, CreateSimulationOrderReqDto dto) {
        CandleReqDto reqDto = new CandleReqDto();
        reqDto.setCurrencyPair(currencyPair);
        reqDto.setStartTime(dto.getStartTime());
        reqDto.setRealTimeRequest(dto.isRealTimeRequest());
        return reqDto;
    }
}
