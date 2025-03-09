package app.traderslave.repository;

import app.traderslave.model.domain.SimulationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SimulationEventRepository extends JpaRepository<SimulationEvent, Long> {

    List<SimulationEvent> findBySimulationIdOrderByEventTimeAsc(Long simulationId);
    List<SimulationEvent> findBySimulationIdOrderByEventTimeDesc(Long simulationId);

    void deleteBySimulationId(Long simulationId);

    default Optional<SimulationEvent> findLatestEventBySimulationId(Long simulationId) {
        List<SimulationEvent> events = findBySimulationIdOrderByEventTimeDesc(simulationId);
        return events.isEmpty() ? Optional.empty() : Optional.of(events.get(0));
    }

}