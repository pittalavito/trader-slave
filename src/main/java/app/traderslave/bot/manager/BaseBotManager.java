package app.traderslave.bot.manager;

import app.traderslave.bot.BotConfig;

public interface BaseBotManager<R> {
    R get(Long botId);

    Long start(BotConfig config);

    void stop(Long botId);

    void restart(Long botId);

    void updateConfig(Long botId, BotConfig config);
}