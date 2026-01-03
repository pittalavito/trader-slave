package app.traderslave.model;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.model.enums.PatternType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pattern {

    private PatternType patternType;
    private List<CandleResDto> candles;
    private boolean breakoutConfirmed;

    public PatternType.Direction getDirection() {
        return patternType.getDirection();
    }

    public PatternType.Category getCategory() {
        return patternType.getCategory();
    }

    public String getDescription() {
        return patternType.getDescription();
    }
}

