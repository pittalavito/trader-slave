package app.traderslave.service;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.exception.custom.EntityNotFoundException;
import app.traderslave.factory.SimulationOrderEntityFactory;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.repository.SimulationOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationOrderService {

    private final SimulationOrderRepository repository;

    public SimulationOrder create(Simulation simulation, CreateSimulationOrderReqDto dto, CandleResDto candle) {
        return repository.save(SimulationOrderEntityFactory.create(simulation, dto, candle));
    }

    public SimulationOrder findByIdOrError(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SimulationOrder.class.getSimpleName()));
    }

}
