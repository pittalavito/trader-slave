package app.traderslave.remote.adapter;

import app.traderslave.controller.dto.CandlesReqDto;
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
}