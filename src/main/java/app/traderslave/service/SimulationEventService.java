package app.traderslave.service;

import app.traderslave.factory.SimulationEventFactory;
import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.repository.SimulationEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class SimulationEventService {

    private final SimulationEventRepository repository;

    public SimulationEvent create(SimulationOrder order, boolean endSimulation) {
        return repository.save(endSimulation ?
                SimulationEventFactory.closeSImulation(order) :
                SimulationEventFactory.create(order));
    }

    public List<SimulationEvent> findBySimulationIdOrderByEventTimeAsc(Long simulationId) {
        return repository.findBySimulationIdOrderByEventTimeAsc(simulationId);
    }

    public SimulationEvent findLatestEventBySimulationId(Long simulationId) {
        return repository.findLatestEventBySimulationId(simulationId);
    }

    @Transactional
    public void deleteBySimulationId(Long simulationId) {
        repository.deleteBySimulationId(simulationId);
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }
}
