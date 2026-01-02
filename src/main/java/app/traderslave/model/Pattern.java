package app.traderslave.model;

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
    private List<Candle> candles;
    private boolean breakoutConfirmed;

    public PatternType.Direction getDirection() {
        return patternType.getDirection();
    }

    public PatternType.PatternCategory getCategory() {
        return patternType.getCategory();
    }
}

