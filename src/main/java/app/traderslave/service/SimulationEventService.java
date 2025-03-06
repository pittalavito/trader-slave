package app.traderslave.service;

import app.traderslave.factory.SimulationEventFactory;
import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.repository.SimulationEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class SimulationEventService {

    private final SimulationEventRepository repository;

    public SimulationEvent create(SimulationOrder order) {
        return repository.save(SimulationEventFactory.create(order));
    }

    public List<SimulationEvent> findBySimulationIdOrderByEventTimeAsc(Long simulationId) {
        return repository.findBySimulationIdOrderByEventTimeAsc(simulationId);
    }

}
