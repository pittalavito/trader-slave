package app.traderslave.remote.assembler;

import app.traderslave.model.dto.CandleDto;
import app.traderslave.utils.TimeUtils;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
public class BinanceClientAssembler {

    public List<CandleDto> toModel(List<Object[]> response) {
        return response.stream()
                .map(this::toModel)
                .toList();
    }

    private CandleDto toModel(Object[] candles) {
        return CandleDto.builder()
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
