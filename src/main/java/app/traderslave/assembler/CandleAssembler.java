package app.traderslave.assembler;

import app.traderslave.domain.model.BackTestBinanceCandle;
import app.traderslave.model.dto.CandleDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class CandleAssembler {

    public List<CandleDto> toModel(List<BackTestBinanceCandle> responseList) {
        responseList.sort(Comparator.comparing(BackTestBinanceCandle::getCloseTime));
        return responseList.stream()
                .map(this::toModel)
                .toList();
    }

    public CandleDto toModel(BackTestBinanceCandle candle) {
        return CandleDto.builder()
                .openTime(candle.getOpenTime())
                .closeTime(candle.getCloseTime())
                .open(candle.getOpen())
                .high(candle.getHigh())
                .low(candle.getLow())
                .close(candle.getClose())
                .volume(candle.getVolume())
                .quoteAssetVolume(candle.getQuoteAssetVolume())
                .build();
    }
}
