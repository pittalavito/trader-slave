package app.traderslave.remote.adapter;

import app.traderslave.model.dto.req.CandleReqDto;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.remote.dto.BinanceGetKlinesReqDto;
import app.traderslave.utils.TimeUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;

@Component
public class BinanceClientAdapter {

    private final static int LIMIT_GET_KLINE = 1000;

    public BinanceGetKlinesReqDto adapt(CandlesReqDto dto) {
        LocalDateTime endTime = dto.isRealTimeRequest() ? TimeUtils.now().minusSeconds(2) : dto.getEndTime();
        LocalDateTime startTime = dto.isRealTimeRequest() ? TimeUtils.calculateStartDate(endTime, dto.getTimeFrame(), dto.getLastNumCandle()) : dto.getStartTime();
        return adapt(
                dto.getCurrencyPair(),
                startTime,
                endTime,
                dto.getTimeFrame(),
                LIMIT_GET_KLINE
        );
    }

    public BinanceGetKlinesReqDto adapt(CandleReqDto dto) {
        LocalDateTime endTime = dto.isRealTimeRequest() ? TimeUtils.now().minusSeconds(1) : dto.getStartTime();
        return adapt(
                dto.getCurrencyPair(),
                endTime.minusSeconds(2),
                endTime,
                TimeFrame.ONE_SECOND,
                1
        );
    }

    private BinanceGetKlinesReqDto adapt(CurrencyPair currencyPair, LocalDateTime starTime, LocalDateTime endTime, TimeFrame timeFrame, Integer limit) {
        return BinanceGetKlinesReqDto.builder()
                .symbol(StringUtils.replace(currencyPair.name(), "_", ""))
                .interval(timeFrame.getCode())
                .startTime(TimeUtils.convertToMillisecond(starTime))
                .endTime(TimeUtils.convertToMillisecond(endTime))
                .limit(limit)
                .build();
    }
}