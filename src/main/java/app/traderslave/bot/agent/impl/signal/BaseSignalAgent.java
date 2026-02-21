package app.traderslave.bot.agent.impl.signal;

import app.traderslave.bot.agent.dto.MarketDataAgentDto;
import app.traderslave.bot.agent.dto.SignalAgentDto;

public abstract class BaseSignalAgent<T extends SignalAgentDto> {

    public abstract T generate(MarketDataAgentDto request);

}
