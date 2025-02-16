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
        BinanceServiceChecker.validateDatesGetKline(dto.getTimeFrame(), dto.getStartDate(), dto.getEndDate(), LIMIT_NUM_CANDLES);
        BinanceGetKlinesRequestDto clientReqDto = BinanceApiRequestAdapter.adapt(dto);
        return fetchCandleSticks(new ArrayList<>(), clientReqDto)
                .collectList()
                .flatMap(BinanceServiceAssembler::toModel);
    }

    private Flux<CandleResDto> fetchCandleSticks(List<CandleResDto> accumulatedCandlesList, BinanceGetKlinesRequestDto clientReqDto) {
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


