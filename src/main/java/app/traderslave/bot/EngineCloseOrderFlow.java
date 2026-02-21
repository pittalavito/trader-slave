package app.traderslave.bot;

import app.traderslave.bot.agent.dto.MarketDataAgentDto;
import app.traderslave.bot.agent.dto.PortfolioAgentDto;
import app.traderslave.bot.agent.dto.RiskAgentDto;
import app.traderslave.bot.agent.dto.SignalAgentDto;
import org.springframework.util.CollectionUtils;

public record EngineCloseOrderFlow(
        MarketDataAgentDto marketData,
        PortfolioAgentDto portfolio,
        RiskAgentDto risk,
        SignalAgentDto signal
) {

    public boolean noOpenOrders() {
        return CollectionUtils.isEmpty(portfolio.getOpenOrders());
    }
}
