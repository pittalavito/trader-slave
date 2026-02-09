package app.traderslave.domain.repository;

import app.traderslave.domain.model.BackTestPortfolioEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BackTestPortfolioEventRepository extends JpaRepository<BackTestPortfolioEvent, Long> {

    List<BackTestPortfolioEvent> findBySimulationIdOrderByEventTimeAsc(Long simulationId);
    List<BackTestPortfolioEvent> findBySimulationIdOrderByEventTimeDesc(Long simulationId);

    void deleteBySimulationId(Long simulationId);

    default BackTestPortfolioEvent findLatestEventBySimulationId(Long simulationId) {
        List<BackTestPortfolioEvent> events = findBySimulationIdOrderByEventTimeDesc(simulationId);
        return events.stream().findFirst().orElse(null);
    }

}