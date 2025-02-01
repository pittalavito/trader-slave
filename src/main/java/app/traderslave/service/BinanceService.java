package app.traderslave.service;

import app.traderslave.assembler.BinanceServiceAssembler;
import app.traderslave.controller.dto.CandleResponseDto;
import app.traderslave.controller.dto.CandlesRequestDto;
import app.traderslave.controller.dto.CandlesResponseDto;
import app.traderslave.remote.BaseRemoteTrainingInterface;
import app.traderslave.remote.adapter.BinanceApiRequestAdapter;
import app.traderslave.remote.adapter.BinanceApiResponseAdapter;
import app.traderslave.remote.api.BinanceApi;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import app.traderslave.utility.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BinanceService extends BaseService implements BaseRemoteTrainingInterface {

    private final BinanceApi binanceApi;

    @Override
    public Mono<CandlesResponseDto> getCandleSticks(CandlesRequestDto dto) {
        List<CandleResponseDto> responseList = new ArrayList<>();

        long startDateRequest = TimeUtils.convertToUTCMillisecond(dto.getStartDate());
        long endDateRequest = TimeUtils.convertToUTCMillisecond(dto.getEndDate());

        while (true) {
            List<CandleResponseDto> response = callBinanceKline(dto, startDateRequest, endDateRequest);
            if (CollectionUtils.isEmpty(response)) {
                break;
            }
            CandleResponseDto lastCandle = response.get(response.size() - 1);
            startDateRequest = TimeUtils.convertToUTCMillisecond(lastCandle.getCloseTime());
            responseList.addAll(response);
        }

        return BinanceServiceAssembler.toModel(responseList);
    }

    private List<CandleResponseDto> callBinanceKline(CandlesRequestDto dto, Long startDateRequest, Long endDateRequest) {
        BinanceGetKlinesRequestDto requestDto = BinanceApiRequestAdapter.adapt(dto.getCurrencyPair(), startDateRequest, endDateRequest, dto.getTimeFrame().getCode());

        Mono<List<Object[]>> response = binanceApi.getKlines(requestDto);

        return BinanceApiResponseAdapter.adapt(Objects.requireNonNull(response.block()));
    }
}
