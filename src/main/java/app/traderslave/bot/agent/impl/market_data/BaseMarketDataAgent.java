package app.traderslave.bot.agent.impl.market_data;

import app.traderslave.bot.agent.dto.MarketDataAgentDto;

public abstract class BaseMarketDataAgent<R> {

    public abstract MarketDataAgentDto get(R request);
}
