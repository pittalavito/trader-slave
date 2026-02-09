package app.traderslave.remote.service;

import app.traderslave.checker.BinanceChecker;
import app.traderslave.model.dto.req.CandleReqDto;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.remote.adapter.BinanceClientAdapter;
import app.traderslave.remote.assembler.BinanceClientAssembler;
import app.traderslave.remote.client.BinanceClient;
import app.traderslave.remote.dto.BinanceGetKlinesReqDto;
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

    private final BinanceClient client;
    private final BinanceClientAdapter adapter;
    private final BinanceClientAssembler assembler;

    public Mono<CandleDto> findCandleAsync(CandleReqDto dto) {
        BinanceChecker.checkDatesGetKline(dto);
        BinanceGetKlinesReqDto clientReqDto = adapter.adapt(dto);
        return client.getKlines(clientReqDto)
                .map(assembler::toModel)
                .filter(CollectionUtils::hasUniqueObject)
                .map(CollectionUtils::firstElement);
    }

    public CandleDto findCandleSync(CandleReqDto dto) {
        var blockingMono = findCandleAsync(dto).block();
        if (blockingMono == null) {
            throw new RuntimeException("Candle not found");
        }
        return blockingMono;
    }

    public Mono<List<CandleDto>> findCandlesAsync(CandlesReqDto dto) {
        BinanceChecker.checkDatesGetKline(dto);
        BinanceGetKlinesReqDto clientReqDto = adapter.adapt(dto);
        return fetchCandleSticks(new HashSet<>(), clientReqDto)
                .collectList();
    }

    public List<CandleDto> findCandlesSync(CandlesReqDto dto) {
        var blockingMono = findCandlesAsync(dto).block();
        if (blockingMono == null) {
            throw new RuntimeException("Candles not found");
        }
        return blockingMono;
    }

    private Flux<CandleDto> fetchCandleSticks(Set<CandleDto> accumulatedCandlesList, BinanceGetKlinesReqDto clientReqDto) {
        return client.getKlines(clientReqDto)
                .flatMapMany(clientRes -> {
                    if (!CollectionUtils.isEmpty(clientRes)) {
                        accumulatedCandlesList.addAll(assembler.toModel(clientRes));
                        clientReqDto.setStartTime(((Long) clientRes.get(clientRes.size() - 1)[6]) + 1);
                        return fetchCandleSticks(accumulatedCandlesList, clientReqDto);
                    }
                    return Flux.fromIterable(accumulatedCandlesList);
                });
    }
}