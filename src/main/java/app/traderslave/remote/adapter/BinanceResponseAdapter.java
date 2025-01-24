package app.traderslave.remote.adapter;

import app.traderslave.remote.dto.BinanceGetKlinesResponseDto;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BinanceResponseAdapter {

    public BinanceGetKlinesResponseDto adaptKlinesResponseDto(List<List<Object>> response) {
        List<BinanceGetKlinesResponseDto.KlineDTO> klineList = response.stream()
                .map(BinanceResponseAdapter::adaptKlineDto)
                .collect(Collectors.toList());
        return BinanceGetKlinesResponseDto.builder()
                .list(klineList)
                .build();
    }

    private BinanceGetKlinesResponseDto.KlineDTO adaptKlineDto(List<Object> kline) {
        BinanceGetKlinesResponseDto.KlineDTO dto = new BinanceGetKlinesResponseDto.KlineDTO();
        dto.setOpenTime((Long) kline.get(0));
        dto.setOpen(new BigDecimal(kline.get(1).toString()));
        dto.setHigh(new BigDecimal(kline.get(2).toString()));
        dto.setLow(new BigDecimal(kline.get(3).toString()));
        dto.setClose(new BigDecimal(kline.get(4).toString()));
        dto.setVolume(new BigDecimal(kline.get(5).toString()));
        dto.setCloseTime((Long) kline.get(6));
        dto.setQuoteAssetVolume(new BigDecimal(kline.get(7).toString()));
        dto.setNumberOfTrades((Integer) kline.get(8));
        dto.setTakerBuyBaseAssetVolume(new BigDecimal(kline.get(9).toString()));
        dto.setTakerBuyQuoteAssetVolume(new BigDecimal(kline.get(10).toString()));
        dto.setIgnore(kline.get(11).toString());
        return dto;
    }
}
