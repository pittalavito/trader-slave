package app.traderslave.assembler;

import app.traderslave.domain.model.CandleBackTest;
import app.traderslave.model.Candle;
import lombok.experimental.UtilityClass;
import java.util.Comparator;
import java.util.List;

@UtilityClass
public class CandleAssembler {

    public List<Candle> toModel(List<CandleBackTest> responseList) {
        responseList.sort(Comparator.comparing(CandleBackTest::getCloseTime));
        return responseList.stream()
                .map(CandleAssembler::toModel)
                .toList();
    }

    public Candle toModel(CandleBackTest candle) {
        return Candle.builder()
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
