package app.traderslave.bot.engine;

import app.traderslave.bot.service.portfolio.BackTestPortfolioServiceBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class BackTestEngine extends BaseTradingEngine {

    private final BackTestPortfolioServiceBotService portfolioImpl;
    //private final MarketData marketData;
    //private final TradingSignals tradingSignals;
    //private final RiskManager riskManager;
    //private final PositionSizer positionSizer;
    //private final OrderExecutor orderExecutor;

    @Override
    protected void whileExecuting() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("BackTestEngine execution interrupted", e);
        }
    }

}
