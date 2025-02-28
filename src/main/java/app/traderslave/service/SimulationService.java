package app.traderslave.service;

import app.traderslave.exception.custom.EntityNotFoundException;
import app.traderslave.factory.SimulationEntityFactory;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.repository.SimulationRepository;
import app.traderslave.model.domain.Simulation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationService {

    private final SimulationRepository repository;

    public Simulation create(CurrencyPair currencyPair) {
        return repository.save(SimulationEntityFactory.create(currencyPair));
    }

    public BigDecimal updateBalance(Simulation simulation, BigDecimal amountOfTrade) {
        repository.save(SimulationEntityFactory.updateBalance(simulation, amountOfTrade));
        return simulation.getBalance();
    }

    public Simulation findByIdOrError(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation"));
    }

}
