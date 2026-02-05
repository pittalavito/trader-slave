package app.traderslave.domain.service;

import app.traderslave.model.dto.req.CreateSimulationReqDto;
import app.traderslave.model.dto.req.TimeReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.domain.factory.SimulationFactory;
import app.traderslave.domain.model.Simulation;
import app.traderslave.domain.model.SimulationOrder;
import app.traderslave.domain.repository.SimulationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationDomainService {

    private final SimulationRepository repository;

    public Simulation create(CreateSimulationReqDto dto) {
        return repository.save(SimulationFactory.create(dto));
    }

    public void addBalance(Simulation simulation, SimulationOrder order) {
        repository.save(SimulationFactory.addBalance(simulation, order));
    }

    public void subtractBalance(Simulation simulation, SimulationOrder order) {
        repository.save(SimulationFactory.subtractBalance(simulation, order));
    }

    public Simulation close(Simulation simulation, TimeReqDto dto) {
        return repository.save(SimulationFactory.close(simulation, dto));
    }

    public Simulation findByIdOrError(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionEnum.SIMULATION_NOT_FOUND));
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }
}
