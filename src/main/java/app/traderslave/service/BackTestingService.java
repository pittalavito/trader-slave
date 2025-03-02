package app.traderslave.service;

import app.traderslave.command.*;
import app.traderslave.controller.dto.*;
import app.traderslave.model.enums.CurrencyPair;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestingService {

    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;

    @Transactional
    public Mono<PostSimulationResDto> createSimulation(CreateSimulationReqDto dto) {
        CreateSimulationCommand command = new CreateSimulationCommand(dto, simulationService);
        return command.execute();
    }

    @Transactional
    public Mono<SimulationOrderResDto> createOrder(CreateSimulationOrderReqDto reqDto) {
        CreateSimulationOrderCommand command = new CreateSimulationOrderCommand(reqDto, binanceService, simulationService, simulationOrderService);
        return command.execute();
    }

    @Transactional
    public Mono<SimulationOrderResDto> closeOrder(Long orderId, CloseSimulationOrderReqDto reqDto) {
        CloseOrderSimulationCommand command = new CloseOrderSimulationCommand(reqDto, orderId, binanceService, simulationService, simulationOrderService);
        return command.execute();
    }

    @Transactional
    public Mono<GetSimulationResDto> getSimulation(Long id, LocalDateTime time) {
        GetSimulationStatusCommand command = new GetSimulationStatusCommand(id, time, binanceService, simulationService, simulationOrderService);
        return command.execute();
    }

    @Transactional
    public Mono<GetSimulationResDto> closeSimulation(Long id, LocalDateTime time, boolean physicallyDelete) {
        CloseSimulationCommand command = new CloseSimulationCommand(id, time, physicallyDelete, binanceService, simulationService, simulationOrderService);
        return command.execute();
    }
}
