package app.traderslave.service;

import app.traderslave.assembler.BinanceServiceAssembler;
import app.traderslave.checker.BinanceServiceChecker;
import app.traderslave.controller.dto.CandleResponseDto;
import app.traderslave.controller.dto.CandlesRequestDto;
import app.traderslave.controller.dto.CandlesResponseDto;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.remote.BaseRemoteSeachDataInterface;
import app.traderslave.remote.adapter.BinanceApiRequestAdapter;
import app.traderslave.remote.adapter.BinanceApiResponseAdapter;
import app.traderslave.remote.api.BinanceApi;
import app.traderslave.utility.TimeUtils;
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
public class BinanceService extends BaseService implements BaseRemoteSeachDataInterface {

    private static final Long LIMIT_NUM_CANDLES = 100000L;
    private final BinanceApi binanceApi;

    @Override
    public Mono<CandlesResponseDto> getCandleSticks(CandlesRequestDto dto) {
        List<CandleResponseDto> accumulationCandlesList = new ArrayList<>();

        Long startDateMillisecond = TimeUtils.convertToUTCMillisecondOrDefault(dto.getStartDate() , null);
        Long endDateMillisecond = TimeUtils.convertToUTCMillisecondOrDefault(dto.getEndDate(), null);

        BinanceServiceChecker.validateDatesGetKline(dto.getTimeFrame(), startDateMillisecond, endDateMillisecond, LIMIT_NUM_CANDLES);

        return fetchCandlesRecursive(accumulationCandlesList, startDateMillisecond, endDateMillisecond, dto.getTimeFrame(), dto.getCurrencyPair())
                .collectList()
                .flatMap(BinanceServiceAssembler::toModel);
    }

    private Flux<CandleResponseDto> fetchCandlesRecursive(List<CandleResponseDto> accumulatedCandlesList, Long startDateMillisecond, Long endDateMillisecond, TimeFrame timeFrame, CurrencyPair currencyPair) {
        return binanceApi
                .getKlines(BinanceApiRequestAdapter.adapt(currencyPair, startDateMillisecond, endDateMillisecond, timeFrame.getCode()))
                .flatMapMany(binanceResponseList -> {
                    if (CollectionUtils.isEmpty(binanceResponseList)) {
                        return Flux.empty();
                    }
                    List<CandleResponseDto> partialCandlesList = BinanceApiResponseAdapter.adapt(binanceResponseList);
                    CandleResponseDto lastCandle = partialCandlesList.get(partialCandlesList.size() - 1);
                    Long newStartDateMillisecond = TimeUtils.convertToUTCMillisecond(lastCandle.getCloseTime()) + 1;
                    accumulatedCandlesList.addAll(partialCandlesList);

                    return partialCandlesList.size() >= BinanceApiRequestAdapter.LIMIT_GET_KLINE ?
                            fetchCandlesRecursive(accumulatedCandlesList, newStartDateMillisecond, endDateMillisecond, timeFrame, currencyPair) :
                            Flux.fromIterable(accumulatedCandlesList);
                });
    }
}


