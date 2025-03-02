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

    private final int LIMIT_GET_KLINE = 1000;

    /**
     * Find candles
     */
    public BinanceGetKlinesRequestDto adapt(CandlesReqDto dto) {
        LocalDateTime endTime = dto.isRealTimeCandles() ? TimeUtils.now().minusSeconds(2) : dto.getStartDate();
        LocalDateTime startTime = dto.isRealTimeCandles() ? TimeUtils.calculateStartDate(endTime, dto.getTimeFrame(), dto.getLastNumCandle()) : dto.getStartDate();

        return adapt(
                dto.getCurrencyPair(),
                startTime,
                endTime,
                dto.getTimeFrame(),
                LIMIT_GET_KLINE
        );
    }

    /**
     * Find candle
     */
    public BinanceGetKlinesRequestDto adapt(CandleReqDto dto) {
        LocalDateTime time = dto.isRealTimeCandle() ? TimeUtils.now().minusSeconds(1) : dto.getTime();

        return adapt(
                dto.getCurrencyPair(),
                time.minusSeconds(2),
                time,
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