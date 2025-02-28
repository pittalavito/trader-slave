package app.traderslave.repository;

import app.traderslave.model.domain.SimulationOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationOrderRepository extends JpaRepository<SimulationOrder, Long> {
}
