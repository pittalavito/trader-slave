package app.traderslave.service;

import app.traderslave.controller.dto.CreateSimulationReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.factory.SimulationEntityFactory;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.repository.SimulationRepository;
import app.traderslave.model.domain.Simulation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationService {

    private final SimulationRepository repository;

    public Simulation create(CreateSimulationReqDto dto) {
        return repository.save(SimulationEntityFactory.create(dto));
    }

    public Simulation subtractBalance(Simulation simulation, SimulationOrder order) {
        return repository.save(SimulationEntityFactory.subtractBalance(simulation, order));
    }

    public Simulation addBalance(Simulation simulation, SimulationOrder order) {
        return repository.save(SimulationEntityFactory.addBalance(simulation, order));
    }

    public Simulation findByIdOrError(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionEnum.SIMULATION_NOT_FOUND));
    }

}
