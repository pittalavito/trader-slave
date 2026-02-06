package app.traderslave.command;

import app.traderslave.command.base.BaseMonoCommand;
import app.traderslave.domain.service.CandleBackTestDomainService;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.remote.service.BinanceRemoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SaveCandleBackTestCommand extends BaseMonoCommand<CandlesReqDto, Void> {

    private final BinanceRemoteService binanceRemoteService;
    private final CandleBackTestDomainService candleBackTestDomainService;

    @Override
    @Transactional
    public Mono<Void> execute() {
        if (!CollectionUtils.isEmpty(candleBackTestDomainService.getCandles(commandRequest))) {
            return Mono.empty();
        }

        return binanceRemoteService
                .findCandlesAsync(commandRequest)
                .doOnNext(response -> candleBackTestDomainService.saveCandleBackTest(commandRequest, response))
                .then();
    }
}
