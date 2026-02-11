package app.traderslave.domain.service;

import app.traderslave.model.dto.req.BackTestCreateOrderReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.domain.factory.BackTestOrderFactory;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.dto.OrderReportDto;
import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.domain.repository.BackTestOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestOrderDomainService {

    private final BackTestOrderRepository repository;

    public BackTestOrder create(BackTestPortfolio backTestPortfolio, BackTestCreateOrderReqDto dto, CandleDto candle) {
        return repository.save(BackTestOrderFactory.create(backTestPortfolio, dto, candle));
    }

    public BackTestOrder close(BackTestOrder order, OrderReportDto report, boolean endSimulation) {
        return repository.save(BackTestOrderFactory.close(order, report, endSimulation));
    }

    public List<BackTestOrder> findAllByPortfolioId(Long simulationId) {
        return repository.findAllBySimulationId(simulationId);
    }

    public List<BackTestOrder> findAllByPortfolioId(Long simulationId, BackTestOrder.Status status) {
        return repository.findAllBySimulationIdAndStatus(simulationId, status);
    }

    public BackTestOrder findByIdAndSimulationIdOrError(Long id, Long simulationId) {
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
