package app.traderslave.remote.adapter;

import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public class BinanceApiRequestAdapter {

    public final int LIMIT_GET_KLINE = 1000;

    public BinanceGetKlinesRequestDto adapt(CandlesReqDto dto) {
        return BinanceGetKlinesRequestDto.builder()
                .symbol(StringUtils.replace(dto.getCurrencyPair().name(), "_", ""))
                .endTime(TimeUtils.convertToUTCMillisecond(dto.getEndDate()))
                .startTime(TimeUtils.convertToUTCMillisecond(dto.getStartDate()))
                .interval(dto.getTimeFrame().getCode())
                .limit(LIMIT_GET_KLINE)
                .build();
    }

    public BinanceGetKlinesRequestDto adapt(CurrencyPair currencyPair, Long startDate, Long endDate, String interval) {
        return BinanceGetKlinesRequestDto.builder()
                .symbol(StringUtils.replace(currencyPair.name(), "_", ""))
                .startTime(startDate)
                .endTime(endDate)
                .interval(interval)
                .limit(LIMIT_GET_KLINE)
                .build();
    }

}
