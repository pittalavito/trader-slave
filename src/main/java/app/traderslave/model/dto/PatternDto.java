package app.traderslave.model.dto;

import app.traderslave.model.enums.PatternType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatternDto {

    private PatternType patternType;
    private List<CandleDto> candles;
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

