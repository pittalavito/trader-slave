package app.traderslave.bot.engine;

import app.traderslave.bot.BotConfig;
import app.traderslave.bot.EngineCloseOrderFlow;
import app.traderslave.bot.EngineOpenOrderFlow;

public abstract class BaseEngine {

    public abstract void init(BotConfig config);

    public abstract void execute(Long botId);

    public abstract void openOrderFlow(EngineOpenOrderFlow flow);

    public abstract void closeOrderFlow(EngineCloseOrderFlow flow);
}
