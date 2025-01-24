package app.traderslave.adapter;

import app.traderslave.controller.dto.GetCandleResponseDto;
import app.traderslave.remote.dto.BinanceGetKlinesResponseDto;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class BinanceServiceAdapter {

    public Mono<GetCandleResponseDto> adapt(Mono<BinanceGetKlinesResponseDto> response) {
        //todo implement
        return null;
    }
}
