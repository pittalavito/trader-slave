package app.traderslave.bot.agent.impl.portfolio;

import app.traderslave.bot.BotConfig;
import app.traderslave.bot.agent.model.PortfolioModel;

public abstract class BasePortfolioAgent<R> {

    public abstract PortfolioModel initialize(BotConfig request);

    public abstract PortfolioModel get(R request);
}
