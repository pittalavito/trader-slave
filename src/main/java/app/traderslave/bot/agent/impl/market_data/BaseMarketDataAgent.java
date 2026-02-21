package app.traderslave.bot.agent.impl.market_data;

import app.traderslave.bot.agent.model.MarketDataModel;

public abstract class BaseMarketDataAgent<R> {

    public abstract MarketDataModel get(R request);
}
