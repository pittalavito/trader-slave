package app.traderslave.bot.domain.service;

import app.traderslave.bot.domain.model.BackTestBot;
import app.traderslave.bot.domain.repository.BackTestBotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestBotDomainService {

    private final BackTestBotRepository repository;

    public BackTestBot findByIdOrError(Long botId) {
        return repository.findById(botId)
                .orElseThrow(() -> new RuntimeException("Bot not found with id: " + botId));
    }

    public BackTestBot save(BackTestBot bot) {
        return repository.save(bot);
    }
}
