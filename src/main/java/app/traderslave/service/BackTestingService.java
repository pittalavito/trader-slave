package app.traderslave.service;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.assembler.BackTestingServiceAssembler;
import app.traderslave.checker.BackTestingServiceChecker;
import app.traderslave.checker.TimeChecker;
import app.traderslave.controller.dto.*;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.CurrencyPair;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestingService {

    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;

    @Transactional
    public Mono<PostSimulationResDto> createSimulation(CurrencyPair currencyPair) {
        Simulation simulation = simulationService.create(currencyPair);
        return BackTestingServiceAssembler.toModel(simulation);
    }

    @Transactional
    public Mono<CreateSimulationOrderResDto> createOrder(CreateSimulationOrderReqDto reqDto) {
        TimeChecker.checkStartDate(reqDto.getTime());
        Simulation simulation = simulationService.findByIdOrError(reqDto.getSimulationId());
        BackTestingServiceChecker.checkBalance(simulation, reqDto);

        return binanceService.findCandle(BinanceServiceAdapter.adapt(simulation.getCurrencyPair(), reqDto.getTime()))
                .flatMap(candle -> {
                    SimulationOrder order = simulationOrderService.create(simulation, reqDto, candle);
                    BigDecimal balance = simulationService.updateBalance(simulation, order);
                    return BackTestingServiceAssembler.toModel(order, balance);
                });
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
