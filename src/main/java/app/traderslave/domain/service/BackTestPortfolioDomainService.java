package app.traderslave.domain.service;

import app.traderslave.model.dto.req.BackTestCreatePortfolioReqDto;
import app.traderslave.model.dto.req.TimeReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.domain.factory.BackTestPortfolioFactory;
import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.domain.repository.BackTestPortfolioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestPortfolioDomainService {

    private final BackTestPortfolioRepository repository;

    public BackTestPortfolio create(BackTestCreatePortfolioReqDto dto) {
        return repository.save(BackTestPortfolioFactory.create(dto));
    }

    public void addBalance(BackTestPortfolio backTestPortfolio, BackTestOrder order) {
        repository.save(BackTestPortfolioFactory.addBalance(backTestPortfolio, order));
    }

    public void subtractBalance(BackTestPortfolio backTestPortfolio, BackTestOrder order) {
        repository.save(BackTestPortfolioFactory.subtractBalance(backTestPortfolio, order));
    }

    public BackTestPortfolio close(BackTestPortfolio backTestPortfolio, TimeReqDto dto) {
        return repository.save(BackTestPortfolioFactory.close(backTestPortfolio, dto));
    }

    public BackTestPortfolio findByIdOrError(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionEnum.SIMULATION_NOT_FOUND));
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }
}
