package app.traderslave.service;

import app.traderslave.assembler.BinanceServiceAssembler;
import app.traderslave.checker.BinanceServiceChecker;
import app.traderslave.checker.TimeChecker;
import app.traderslave.controller.dto.CandleReqDto;
import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.adapter.BinanceApiRequestAdapter;
import app.traderslave.adapter.BinanceApiResponseAdapter;
import app.traderslave.remote.api.BinanceApi;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BinanceService {

    public static final int LIMIT_NUM_CANDLES = 50000;

    private final BinanceApi binanceApi;

    public Mono<CandleResDto> findCandle(CandleReqDto dto) {
        if (!dto.isRealTimeCandle()) {
            TimeChecker.checkStartDate(dto.getTime());
        }

        return binanceApi.getKlines(BinanceApiRequestAdapter.adapt(dto))
                .map(BinanceApiResponseAdapter::adapt)
                .filter(CollectionUtils::hasUniqueObject)
                .map(CollectionUtils::firstElement);
    }

    public Mono<CandlesResDto> findCandles(CandlesReqDto dto) {
        if (!dto.isRealTimeCandles()) {
            BinanceServiceChecker.checkDatesGetKline(dto.getTimeFrame(), dto.getStartDate(), dto.getEndDate(), LIMIT_NUM_CANDLES);
        }

        return fetchCandleSticks(new HashSet<>(), BinanceApiRequestAdapter.adapt(dto))
                .collectList()
                .flatMap(BinanceServiceAssembler::toModel);
    }

    private Flux<CandleResDto> fetchCandleSticks(Set<CandleResDto> accumulatedCandlesList, BinanceGetKlinesRequestDto clientReqDto) {
        return binanceApi.getKlines(clientReqDto)
                .flatMapMany(clientRes -> {
                    if (!CollectionUtils.isEmpty(clientRes)) {
                        accumulatedCandlesList.addAll(BinanceApiResponseAdapter.adapt(clientRes));
                        // close time last candle response in millisecond + 1
                        clientReqDto.setStartTime(((Long) clientRes.get(clientRes.size() - 1)[6]) + 1);
                        return fetchCandleSticks(accumulatedCandlesList, clientReqDto);
                    }
                    return Flux.fromIterable(accumulatedCandlesList);
                });
    }

}


