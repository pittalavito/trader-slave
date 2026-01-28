package app.traderslave.assembler;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.model.domain.BinanceCandleBackTest;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;
import java.util.Comparator;
import java.util.List;

@UtilityClass
public class CandlesResDtoAssembler {

    public Mono<CandlesResDto> toModel(List<CandleResDto> responseList) {
        responseList.sort(Comparator.comparing(CandleResDto::getCloseTime));
        return Mono.just(
                CandlesResDto.builder()
                        .list(responseList)
                        .size(responseList.size())
                        .build()
        );
    }

    public CandlesResDto toModelBackTest(List<BinanceCandleBackTest> responseList) {
        var list = responseList.stream()
                .map(CandlesResDtoAssembler::toModel)
                .toList();
        return CandlesResDto.builder()
                .list(list)
                .size(responseList.size())
                .build();
    }

    public CandleResDto toModel(BinanceCandleBackTest candle) {
        return CandleResDto.builder()
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
