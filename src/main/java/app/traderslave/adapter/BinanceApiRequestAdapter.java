package app.traderslave.adapter;

import app.traderslave.controller.dto.CandleReqDto;
import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;

@UtilityClass
public class BinanceApiRequestAdapter {

    public final int LIMIT_GET_KLINE = 1000;

    public BinanceGetKlinesRequestDto adapt(CandlesReqDto dto) {
        return adapt(
                dto.getCurrencyPair(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getTimeFrame(),
                LIMIT_GET_KLINE
        );
    }

    public BinanceGetKlinesRequestDto adapt(CandleReqDto dto) {
        return adapt(
                dto.getCurrencyPair(),
                dto.getTime().minusSeconds(1),
                dto.getTime(),
                TimeFrame.ONE_SECOND,
                1
        );
    }

    private BinanceGetKlinesRequestDto adapt(CurrencyPair currencyPair, LocalDateTime starTime, LocalDateTime endTime, TimeFrame timeFrame, Integer limit) {
        return BinanceGetKlinesRequestDto.builder()
                .symbol(StringUtils.replace(currencyPair.name(), "_", ""))
                .interval(timeFrame.getCode())
                .startTime(TimeUtils.convertToMillisecond(starTime))
                .endTime(TimeUtils.convertToMillisecond(endTime))
                .limit(limit)
                .build();
    }
}