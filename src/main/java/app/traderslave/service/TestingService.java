package app.traderslave.service;

import app.traderslave.command.*;
import app.traderslave.controller.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestingService {

    private final BeanFactory beanFactory;
    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;
    private final SimulationEventService simulationEventService;

    @Transactional
    public Mono<PostSimulationResDto> createSimulation(CreateSimulationReqDto dto) {
        CreateSimulationCommand command = beanFactory.getBean(CreateSimulationCommand.class, dto, simulationService);
        return command.execute();
    }

    @Transactional
    public Mono<SimulationOrderResDto> createOrder(CreateSimulationOrderReqDto dto) {
        CreateSimulationOrderCommand command = beanFactory.getBean(CreateSimulationOrderCommand.class, dto, binanceService, simulationService, simulationOrderService, simulationEventService);
        return command.execute();
    }

    @Transactional
    public Mono<SimulationOrderResDto> getOrder(GetSimulationOrderReqDto dto) {
        GetSimulationOrderCommand command = beanFactory.getBean(GetSimulationOrderCommand.class, dto, binanceService, simulationService, simulationOrderService, simulationEventService);
        return command.execute();
    }

    @Transactional
    public Mono<SimulationOrderResDto> closeOrder(CloseSimulationOrderReqDto dto) {
        CloseSimulationOrderCommand command = beanFactory.getBean(CloseSimulationOrderCommand.class, dto, binanceService, simulationService, simulationOrderService, simulationEventService);
        return command.execute();
    }

    @Transactional
    public Mono<CloseSimulationResDto> closeSimulation(CloseSimulationReqDto dto) {
        CloseSimulationCommand command = beanFactory.getBean(CloseSimulationCommand.class, dto, binanceService, simulationService, simulationOrderService, simulationEventService);
        return command.execute();
    }
}
