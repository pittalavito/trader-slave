package app.traderslave.domain.service;

import app.traderslave.domain.factory.BackTestPortfolioEventFactory;
import app.traderslave.domain.model.BackTestPortfolioEvent;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.domain.repository.BackTestPortfolioEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestPortfolioEventDomainService {

    private final BackTestPortfolioEventRepository repository;

    public BackTestPortfolioEvent create(BackTestOrder order, boolean endSimulation) {
        return repository.save(endSimulation ?
                BackTestPortfolioEventFactory.close(order) :
                BackTestPortfolioEventFactory.create(order));
    }

    public List<BackTestPortfolioEvent> findBySimulationIdOrderByEventTimeAsc(Long simulationId) {
        return repository.findBySimulationIdOrderByEventTimeAsc(simulationId);
    }

    public BackTestPortfolioEvent findLatestEventBySimulationId(Long simulationId) {
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
