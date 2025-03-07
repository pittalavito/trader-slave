package app.traderslave.service;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.factory.SimulationOrderFactory;
import app.traderslave.model.report.OrderReport;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.repository.SimulationOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class SimulationOrderService {

    private final SimulationOrderRepository repository;

    public SimulationOrder create(Simulation simulation, CreateSimulationOrderReqDto dto, CandleResDto candle) {
        return repository.save(SimulationOrderFactory.create(simulation, dto, candle));
    }

    public SimulationOrder close(SimulationOrder order, OrderReport report) {
        return repository.save(SimulationOrderFactory.close(order, report));
    }

    public List<SimulationOrder> findAllBySimulationId(Long simulationId) {
        return repository.findAllBySimulationId(simulationId);
    }

    public SimulationOrder findByIdAndSimulationIdOrError(Long id, Long simulationId) {
        return repository.findByIdAndSimulationId(id, simulationId)
                .orElseThrow(() -> new CustomException(ExceptionEnum.SIMULATION_ORDER_NOT_FOUND));
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
