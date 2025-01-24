package app.traderslave.remote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class BinanceGetKlinesResponseDto {

    private List<KlineDTO> list;

    @Data
    public static class KlineDTO {

        @JsonProperty("open_time")
        private Long openTime;

        @JsonProperty("open")
        private BigDecimal open;

        @JsonProperty("high")
        private BigDecimal high;

        @JsonProperty("low")
        private BigDecimal low;

        @JsonProperty("close")
        private BigDecimal close;

        @JsonProperty("volume")
        private BigDecimal volume;

        @JsonProperty("close_time")
        private Long closeTime;

        @JsonProperty("quote_asset_volume")
        private BigDecimal quoteAssetVolume;

        @JsonProperty("number_of_trades")
        private Integer numberOfTrades;

        @JsonProperty("taker_buy_base_asset_volume")
        private BigDecimal takerBuyBaseAssetVolume;

        @JsonProperty("taker_buy_quote_asset_volume")
        private BigDecimal takerBuyQuoteAssetVolume;

        @JsonProperty("ignore")
        private String ignore;
    }
}
