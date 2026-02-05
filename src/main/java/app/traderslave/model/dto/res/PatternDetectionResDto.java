package app.traderslave.model.dto.res;

import app.traderslave.model.enums.PatternType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PatternDetectionResDto {

    private List<Pattern> patterns;
    private LocalDateTime closeTime;
    private BigDecimal close;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Pattern {
        private PatternType patternType;
        private String direction;
        private String category;
        private String description;
        private boolean breakoutConfirmed;
        private List<Candle> candles;
        private Candle lastCandle;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Candle {
        private LocalDateTime closeTime;
        private BigDecimal close;
    }
}
