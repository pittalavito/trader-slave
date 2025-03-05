package app.traderslave.service;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.factory.SimulationOrderEntityFactory;
import app.traderslave.model.report.ReportOrder;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.SOrderStatus;
import app.traderslave.repository.SimulationOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationOrderService {

    private final SimulationOrderRepository repository;

    public SimulationOrder create(Simulation simulation, CreateSimulationOrderReqDto dto, CandleResDto candle) {
        return repository.save(SimulationOrderEntityFactory.create(simulation, dto, candle));
    }

    public List<SimulationOrder> findBySimulationId(Long simulationId) {
        return repository.findBySimulationId(simulationId);
    }

    public SimulationOrder findByIdOrError(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionEnum.SIMULATION_ORDER_NOT_FOUND));
    }

    public List<SimulationOrder> findBySimulationIdAndStatus(Long simulationId, SOrderStatus status) {
        return repository.findBySimulationIdAndStatus(simulationId, status);
    }

    public SimulationOrder findByIdAndSimulationIdOrError(Long id, Long simulationId) {
        return repository.findByIdAndSimulationId(id, simulationId)
                .orElseThrow(() -> new CustomException(ExceptionEnum.SIMULATION_ORDER_NOT_FOUND));
    }

    public SimulationOrder close(SimulationOrder order, ReportOrder report) {
        return repository.save(SimulationOrderEntityFactory.close(order, report));
    }

}
