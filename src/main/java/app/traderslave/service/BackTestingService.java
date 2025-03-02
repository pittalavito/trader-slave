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
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestingService {

    private final BeanFactory beanFactory;
    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;

    @Transactional
    public Mono<PostSimulationResDto> createSimulation(CreateSimulationReqDto dto) {
        CreateSimulationCommand command = beanFactory.getBean(CreateSimulationCommand.class, dto, simulationService);
        return command.execute();
    }

    @Transactional
    public Mono<SimulationOrderResDto> createOrder(CreateSimulationOrderReqDto dto) {
        CreateSimulationOrderCommand command = beanFactory.getBean(CreateSimulationOrderCommand.class, dto, binanceService, simulationService, simulationOrderService);
        return command.execute();
    }

    @Transactional
    public Mono<SimulationOrderResDto> closeOrder(Long orderId, CloseSimulationOrderReqDto dto) {
        CloseSimulationOrderCommand command = beanFactory.getBean(CloseSimulationOrderCommand.class, dto, orderId, binanceService, simulationService, simulationOrderService);
        return command.execute();
    }

    @Transactional
    public Mono<GetSimulationResDto> getSimulation(Long id, LocalDateTime time) {
        GetSimulationStatusCommand command = beanFactory.getBean(GetSimulationStatusCommand.class, id, time, binanceService, simulationService, simulationOrderService);
        return command.execute();
    }

    @Transactional
    public Mono<GetSimulationResDto> closeSimulation(Long id, LocalDateTime time, boolean physicallyDelete) {
        CloseSimulationCommand command = beanFactory.getBean(CloseSimulationCommand.class, id, time, physicallyDelete, binanceService, simulationService, simulationOrderService);
        return command.execute();
    }

}
