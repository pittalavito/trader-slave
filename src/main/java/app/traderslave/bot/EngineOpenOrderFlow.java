package app.traderslave.bot;

import app.traderslave.bot.agent.dto.PortfolioAgentDto;
import app.traderslave.bot.agent.dto.RiskAgentDto;
import app.traderslave.bot.agent.dto.SignalAgentDto;

public record EngineOpenOrderFlow (
        SignalAgentDto signal,
        PortfolioAgentDto portfolio,
        RiskAgentDto risk
) {
    public boolean noSignal() {
        return signal == null || signal.getNoSignal();
    }
}
