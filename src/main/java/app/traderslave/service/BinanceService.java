package app.traderslave.service;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.controller.dto.GetCandleRequestDto;
import app.traderslave.controller.dto.GetCandleResponseDto;
import app.traderslave.remote.BaseRemoteTrainingInterface;
import app.traderslave.remote.adapter.BinanceRequestAdapter;
import app.traderslave.remote.api.BinanceApi;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import app.traderslave.remote.dto.BinanceGetKlinesResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BinanceService implements BaseRemoteTrainingInterface {

    private final BinanceApi binanceApi;

    @Override
    public Mono<GetCandleResponseDto> getCandle(GetCandleRequestDto dto) {
        BinanceGetKlinesRequestDto requestDto = BinanceRequestAdapter.adapt(dto);
        Mono<BinanceGetKlinesResponseDto> response = binanceApi.getKlines(requestDto);
        return BinanceServiceAdapter.adapt(response);
    }

}
