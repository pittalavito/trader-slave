package app.traderslave.service;

import app.traderslave.exception.custom.EntityNotFoundException;
import app.traderslave.factory.SimulationOrderEntityFactory;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.OrderType;
import app.traderslave.repository.SimulationOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationOrderService {

    private final SimulationOrderRepository repository;

    public SimulationOrder create(Long simulationId, OrderType orderType, BigDecimal amountOfTrade, BigDecimal openPrice, LocalDateTime openTime) {
        return repository.save(SimulationOrderEntityFactory.create(simulationId, orderType, amountOfTrade, openPrice, openTime));
    }

    public SimulationOrder findByIdOrError(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SimulationOrder.class.getSimpleName()));
    }

}
