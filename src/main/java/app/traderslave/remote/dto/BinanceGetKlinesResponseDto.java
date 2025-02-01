package app.traderslave.remote.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BinanceGetKlinesResponseDto {

    private List<KlineDTO> list;

    @Data
    public static class KlineDTO {
        private LocalDateTime openTime;
        private BigDecimal open;
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal close;
        private BigDecimal volume;
        private LocalDateTime closeTime;
        private BigDecimal quoteAssetVolume;
        private Integer numberOfTrades;
        private BigDecimal takerBuyBaseAssetVolume;
        private BigDecimal takerBuyQuoteAssetVolume;
        private String ignore;
    }
}
