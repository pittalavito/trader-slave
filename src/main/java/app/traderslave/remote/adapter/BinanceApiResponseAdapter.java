package app.traderslave.remote.adapter;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BinanceApiResponseAdapter {

    public List<CandleResDto> adapt(List<Object[]> response) {
        return response.stream()
                .map(BinanceApiResponseAdapter::adapt)
                .collect(Collectors.toList());
    }

    private CandleResDto adapt(Object[] candles) {
        return CandleResDto.builder()
                .openTime(TimeUtils.convertToLocalDateTime((Long) candles[0]))
                .open(new BigDecimal(candles[1].toString()))
                .high(new BigDecimal(candles[2].toString()))
                .low(new BigDecimal(candles[3].toString()))
                .close(new BigDecimal(candles[4].toString()))
                .volume(new BigDecimal(candles[5].toString()))
                .closeTime(TimeUtils.convertToLocalDateTime((Long) candles[6]))
                .quoteAssetVolume(new BigDecimal(candles[7].toString()))
                .numberOfTrades((Integer) candles[8])
                .takerBuyBaseAssetVolume(new BigDecimal(candles[9].toString()))
                .takerBuyQuoteAssetVolume(new BigDecimal(candles[10].toString()))
                .ignore(candles[11].toString())
                .build();
    }

}
