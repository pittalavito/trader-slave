package app.traderslave.service;

import app.traderslave.command.backtest.BackTestClosePortfolioCommand;
import app.traderslave.command.backtest.BackTestCreatePortfolioCommand;
import app.traderslave.command.backtest.BackTestPortfolioDeleteAllCommand;
import app.traderslave.domain.service.BackTestPortfolioDomainService;
import app.traderslave.model.dto.req.BackTestClosePortfolioReqDto;
import app.traderslave.model.dto.CloseBackTestPortfolioDto;
import app.traderslave.model.dto.req.BackTestCreatePortfolioReqDto;
import app.traderslave.model.dto.BackTestCreatePortfolioDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestPortfolioService {

    private final BackTestPortfolioDomainService portfolioDomainService;
    private final BackTestCreatePortfolioCommand portfolioCommand;
    private final BackTestClosePortfolioCommand closePortfolioCommand;
    private final BackTestPortfolioDeleteAllCommand portfolioDeleteAllCommand;

    public BigDecimal getBalance(Long simulationId) {
        var simulation = portfolioDomainService.findByIdOrError(simulationId);
        return simulation.getBalance();
    }

    public BackTestCreatePortfolioDto create(BackTestCreatePortfolioReqDto dto) {
        portfolioCommand.setCommandRequest(dto);
        return portfolioCommand.execute();
    }

    @Transactional
    public CloseBackTestPortfolioDto close(BackTestClosePortfolioReqDto dto) {
        closePortfolioCommand.setCommandRequest(dto);
        return closePortfolioCommand.execute();
    }

    @Transactional
    public void deleteAll() {
        portfolioDeleteAllCommand.execute();
    }
}
