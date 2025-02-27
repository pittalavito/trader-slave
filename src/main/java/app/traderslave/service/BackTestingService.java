package app.traderslave.service;

import app.traderslave.assembler.BackTestingServiceAssembler;
import app.traderslave.controller.dto.*;
import app.traderslave.model.domain.Simulation;
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

    @Transactional
    public Mono<PostSimulationResDto> createSimulation(CurrencyPair currencyPair) {
        Simulation simulation = simulationService.create(currencyPair);
        return BackTestingServiceAssembler.toModel(simulation);
    }

    public Mono<Object> createOrderBuy() {
        //todo implement
        return null;
    }

    public Mono<Object> createOrderSell() {
        //todo implement
        return null;
    }

    public Mono<Object> closeOrder() {
        //todo implement
        return null;
    }

    public Mono<Object> getSimulationStatus(Long simulationId) {
        //todo implement
        return null;
    }

    public Mono<Object> closeSimulation(Long id, boolean delete) {
        //todo implement
        return null;
    }

}
