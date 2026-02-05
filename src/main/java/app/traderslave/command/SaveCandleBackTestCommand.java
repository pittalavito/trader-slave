package app.traderslave.command;

import app.traderslave.command.base.BaseCommand;
import app.traderslave.domain.service.CandleBackTestDomainService;
import app.traderslave.remote.service.BinanceRemoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveCandleBackTestCommand extends BaseCommand<Void, Void> {

    private final BinanceRemoteService binanceRemoteService;
    private final CandleBackTestDomainService candleBackTestDomainService;

    @Override
    @Transactional
    public Void execute() {

        return null;
    }
}
