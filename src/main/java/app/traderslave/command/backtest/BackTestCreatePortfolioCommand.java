package app.traderslave.command.backtest;

import app.traderslave.assembler.backtest.BackTestCreatePortfolioDtoAssembler;
import app.traderslave.checker.TimeChecker;
import app.traderslave.command.base.BaseCommand;
import app.traderslave.model.dto.req.BackTestCreatePortfolioReqDto;
import app.traderslave.model.dto.BackTestCreatePortfolioDto;
import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.service.BackTestPortfolioDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackTestCreatePortfolioCommand extends BaseCommand<BackTestCreatePortfolioReqDto, BackTestCreatePortfolioDto> {

    private final BackTestPortfolioDomainService portfolioDomainService;
    private final BackTestCreatePortfolioDtoAssembler createPortfolioDtoAssembler;

    @Override
    public BackTestCreatePortfolioDto execute() {
        TimeChecker.checkStartDate(commandRequest.getStartTime());
        BackTestPortfolio backTestPortfolio = portfolioDomainService.create(commandRequest);
        return createPortfolioDtoAssembler.toModel(backTestPortfolio);
    }
}
