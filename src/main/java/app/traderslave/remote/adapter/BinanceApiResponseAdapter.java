package app.traderslave.remote.adapter;

import app.traderslave.controller.dto.CandleResponseDto;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BinanceApiResponseAdapter {

    public List<CandleResponseDto> adapt(List<Object[]> response) {
        return response.stream()
                .map(BinanceApiResponseAdapter::adapt)
                .collect(Collectors.toList());
    }

    private CandleResponseDto adapt(Object[] candles) {
        return CandleResponseDto.builder()
                .openTime(TimeUtils.convertToUTCLocalDate((Long) candles[0]))
                .open(new BigDecimal(candles[1].toString()))
                .high(new BigDecimal(candles[2].toString()))
                .low(new BigDecimal(candles[3].toString()))
                .close(new BigDecimal(candles[4].toString()))
                .volume(new BigDecimal(candles[5].toString()))
                .closeTime(TimeUtils.convertToUTCLocalDate((Long) candles[6]))
                .quoteAssetVolume(new BigDecimal(candles[7].toString()))
                .numberOfTrades((Integer) candles[8])
                .takerBuyBaseAssetVolume(new BigDecimal(candles[9].toString()))
                .takerBuyQuoteAssetVolume(new BigDecimal(candles[10].toString()))
                .ignore(candles[11].toString())
                .build();
    }

}
