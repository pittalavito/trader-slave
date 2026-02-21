package app.traderslave.bot.agent.impl.portfolio;

import app.traderslave.bot.BotConfig;
import app.traderslave.bot.agent.dto.PortfolioAgentDto;

public abstract class BasePortfolioAgent<R> {

    public abstract PortfolioAgentDto create(BotConfig request);

    public abstract PortfolioAgentDto get(R request);
}
