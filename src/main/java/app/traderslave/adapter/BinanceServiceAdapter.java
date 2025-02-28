package app.traderslave.adapter;

import app.traderslave.controller.dto.CandleReqDto;
import app.traderslave.model.enums.CurrencyPair;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class BinanceServiceAdapter {

    public CandleReqDto adapt(CurrencyPair currencyPair, LocalDateTime time) {
        CandleReqDto reqDto = new CandleReqDto();
        reqDto.setCurrencyPair(currencyPair);
        if (time != null) {
            reqDto.setTime(time);
        }
        return reqDto;
    }

}
