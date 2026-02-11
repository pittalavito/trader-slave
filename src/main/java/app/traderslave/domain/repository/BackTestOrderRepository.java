package app.traderslave.domain.repository;

import app.traderslave.domain.model.BackTestOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BackTestOrderRepository extends JpaRepository<BackTestOrder, Long> {

    List<BackTestOrder> findAllBySimulationId(Long simulationId);

    List<BackTestOrder> findAllBySimulationIdAndStatus(Long simulationId, BackTestOrder.Status status);

    Optional<BackTestOrder> findByIdAndSimulationId(Long id, Long simulationId);

    void deleteBySimulationId(Long simulationId);

}
