package app.traderslave.service;

import app.traderslave.assembler.BinanceServiceAssembler;
import app.traderslave.checker.BinanceServiceChecker;
import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.remote.BaseRemoteSearchDataInterface;
import app.traderslave.remote.adapter.BinanceApiRequestAdapter;
import app.traderslave.remote.adapter.BinanceApiResponseAdapter;
import app.traderslave.remote.api.BinanceApi;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BinanceService extends BaseService implements BaseRemoteSearchDataInterface {

    private static final Long LIMIT_NUM_CANDLES = 50000L;
    private final BinanceApi binanceApi;

    @Override
    public Mono<CandlesResDto> getCandleSticks(CandlesReqDto dto) {
        List<CandleResDto> accumulationCandlesList = new ArrayList<>();
        BinanceServiceChecker.validateDatesGetKline(dto.getTimeFrame(), dto.getStartDate(), dto.getEndDate(), LIMIT_NUM_CANDLES);

        return fetchCandleSticks(accumulationCandlesList, BinanceApiRequestAdapter.adapt(dto))
                .collectList()
                .flatMap(BinanceServiceAssembler::toModel);
    }

    private Flux<CandleResDto> fetchCandleSticks(List<CandleResDto> accumulatedCandlesList, BinanceGetKlinesRequestDto dto) {
        return binanceApi.getKlines(dto)
                .flatMapMany(response -> recallFetchCandleSticksOrResponse(accumulatedCandlesList, response, dto));
    }

    private Flux<CandleResDto> recallFetchCandleSticksOrResponse(List<CandleResDto> accumulatedCandlesList, List<Object[]> binanceResponse, BinanceGetKlinesRequestDto dto) {
        if (!CollectionUtils.isEmpty(binanceResponse)) {
            accumulatedCandlesList.addAll(BinanceApiResponseAdapter.adapt(binanceResponse));
            // close time last candle response in millisecond + 1
            dto.setStartTime(((Long) binanceResponse.get(binanceResponse.size() - 1)[6]) + 1);

            return fetchCandleSticks(accumulatedCandlesList, dto);
        }
        return Flux.fromIterable(accumulatedCandlesList);
    }
}


