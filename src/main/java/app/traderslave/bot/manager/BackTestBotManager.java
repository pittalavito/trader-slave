package app.traderslave.bot.manager;

import app.traderslave.bot.BotConfig;
import app.traderslave.bot.domain.model.BackTestBot;
import app.traderslave.bot.domain.service.BackTestBotDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestBotManager implements BaseBotManager<BackTestBot> {

    private final BackTestBotDomainService domainService;

    @Override
    public Long start(BotConfig config) {
        //todo
        var bot = new BackTestBot();
        bot.setConfig(config);
        bot.setStatus(BackTestBot.Status.RUNNING);
        bot = domainService.save(bot);
        return bot.getId();
    }

    @Override
    public void stop(Long botId) {
        var bot = domainService.findByIdOrError(botId);
        bot.setStatus(BackTestBot.Status.BLOCKED);
        domainService.save(bot);
    }

    @Override
    public void restart(Long botId) {
        var bot = domainService.findByIdOrError(botId);
        bot.setStatus(BackTestBot.Status.RUNNING);
        domainService.save(bot);
    }

    @Override
    public void updateConfig(Long botId, BotConfig config) {
        var bot = domainService.findByIdOrError(botId);
        bot.setConfig(config);
        domainService.save(bot);
    }

    @Override
    public BackTestBot get(Long botId) {
        return domainService.findByIdOrError(botId);
    }
}
