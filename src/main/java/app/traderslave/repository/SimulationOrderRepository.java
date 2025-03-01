package app.traderslave.repository;

import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.SOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SimulationOrderRepository extends JpaRepository<SimulationOrder, Long> {

    List<SimulationOrder> findBySimulationIdAndStatus(Long simulationId, SOrderStatus status);

    Optional<SimulationOrder> findByIdAndSimulationId(Long id, Long simulationId);
}
