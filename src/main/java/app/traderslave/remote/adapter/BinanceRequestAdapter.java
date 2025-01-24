package app.traderslave.remote.adapter;

import app.traderslave.controller.dto.GetCandleRequestDto;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BinanceRequestAdapter {

    public BinanceGetKlinesRequestDto adapt(GetCandleRequestDto dto) {
        int limit = 1000;
        //todo implement
        return new BinanceGetKlinesRequestDto("BTCUSDT", "1h", limit);
    }
}
