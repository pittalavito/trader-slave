package app.traderslave.bot.engine;

import app.traderslave.bot.BotConfig;

public abstract class BaseEngine {

    public abstract void init(BotConfig config);

    public abstract void execute(Long botId);
}
