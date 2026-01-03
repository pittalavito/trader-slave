package app.traderslave.controller.dto;

import app.traderslave.model.enums.PatternType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
public class PatternDetectionResDto {

    private List<Pattern> patterns;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Pattern {
        private PatternType patternType;
        private String direction;
        private String category;
        private String description;
        private boolean breakoutConfirmed;
        private List<CandleResDto> candles;
        private CandleResDto lastCandle;
    }

}
