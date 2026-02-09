package app.traderslave.command.backtest;

import app.traderslave.command.base.BaseMonoCommand;
import app.traderslave.domain.service.BackTestCandleDomainService;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.remote.service.BinanceRemoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BackTestSaveCandlesCommand extends BaseMonoCommand<CandlesReqDto, Void> {

    private final BinanceRemoteService binanceRemoteService;
    private final BackTestCandleDomainService candleDomainService;

    @Override
    @Transactional
    public Mono<Void> execute() {
        if (!CollectionUtils.isEmpty(candleDomainService.getCandles(commandRequest))) {
            return Mono.empty();
        }

        return binanceRemoteService
                .findCandlesAsync(commandRequest)
                .doOnNext(response -> candleDomainService.saveCandleBackTest(commandRequest, response))
                .then();
    }
}
