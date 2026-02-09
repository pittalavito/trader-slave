package app.traderslave.command.backtest;

import app.traderslave.command.base.BaseCommand;
import app.traderslave.domain.service.BackTestPortfolioEventDomainService;
import app.traderslave.domain.service.BackTestPortfolioDomainService;
import app.traderslave.domain.service.BackTestOrderDomainService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackTestPortfolioDeleteAllCommand extends BaseCommand<Void, Void> {

    private final BackTestPortfolioDomainService portfolioDomainService;
    private final BackTestOrderDomainService orderDomainService;
    private final BackTestPortfolioEventDomainService portfolioEventDomainService;

    @Override
    @Transactional
    public Void execute() {
        portfolioEventDomainService.deleteAll();
        orderDomainService.deleteAll();
        portfolioDomainService.deleteAll();
        return null;
    }
}
