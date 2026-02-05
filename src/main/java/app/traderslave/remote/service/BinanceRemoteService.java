package app.traderslave.remote.service;

import app.traderslave.checker.BinanceChecker;
import app.traderslave.controller.dto.*;
import app.traderslave.model.Candle;
import app.traderslave.remote.adapter.BinanceClientRequestAdapter;
import app.traderslave.remote.adapter.BinanceClientResponseAdapter;
import app.traderslave.remote.client.BinanceClient;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BinanceRemoteService {

    private final BinanceClient binanceClient;

    public Mono<Candle> findCandleAsync(CandleReqDto dto) {
        BinanceChecker.checkDatesGetKline(dto);
        BinanceGetKlinesRequestDto clientReqDto = BinanceClientRequestAdapter.adapt(dto);
        return binanceClient.getKlines(clientReqDto)
                .map(BinanceClientResponseAdapter::adapt)
                .filter(CollectionUtils::hasUniqueObject)
                .map(CollectionUtils::firstElement);
    }

    public Candle findCandleSync(CandleReqDto dto) {
        var blockingMono = findCandleAsync(dto).block();
        if (blockingMono == null) {
            throw new RuntimeException("Candle not found");
        }
        return blockingMono;
    }

    public Mono<List<Candle>> findCandlesAsync(CandlesReqDto dto) {
        BinanceChecker.checkDatesGetKline(dto);
        BinanceGetKlinesRequestDto clientReqDto = BinanceClientRequestAdapter.adapt(dto);
        return fetchCandleSticks(new HashSet<>(), clientReqDto)
                .collectList();
    }

    public List<Candle> findCandlesSync(CandlesReqDto dto) {
        var blockingMono = findCandlesAsync(dto).block();
        if (blockingMono == null) {
            throw new RuntimeException("Candles not found");
        }
        return blockingMono;
    }

    private Flux<Candle> fetchCandleSticks(Set<Candle> accumulatedCandlesList, BinanceGetKlinesRequestDto clientReqDto) {
        return binanceClient.getKlines(clientReqDto)
                .flatMapMany(clientRes -> {
                    if (!CollectionUtils.isEmpty(clientRes)) {
                        accumulatedCandlesList.addAll(BinanceClientResponseAdapter.adapt(clientRes));
                        clientReqDto.setStartTime(((Long) clientRes.get(clientRes.size() - 1)[6]) + 1);
                        return fetchCandleSticks(accumulatedCandlesList, clientReqDto);
                    }
                    return Flux.fromIterable(accumulatedCandlesList);
                });
    }
}