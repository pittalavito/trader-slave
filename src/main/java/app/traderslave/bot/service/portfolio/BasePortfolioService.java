package app.traderslave.bot.service.portfolio;

import app.traderslave.bot.dto.PortfolioBotModel;

public abstract class BasePortfolioService<R> {

    public abstract PortfolioBotModel get(R request);
}
