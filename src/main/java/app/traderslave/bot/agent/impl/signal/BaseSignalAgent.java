package app.traderslave.bot.agent.impl.signal;

import app.traderslave.bot.agent.model.MarketDataModel;

public abstract class BaseSignalAgent<T> {

    public abstract T generate(MarketDataModel request);

}
