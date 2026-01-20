package app.traderslave.utility;

import app.traderslave.controller.dto.PatternDetectionResDto;
import app.traderslave.model.enums.Signal;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

@UtilityClass
public class SignalUtils {

    public Signal generateLastSignal(PatternDetectionResDto dto) {
        Signal signal;
        if (CollectionUtils.isEmpty(dto.getPatterns())) {
            signal = Signal.NONE;
        } else {
            PatternDetectionResDto.Pattern latestPattern = dto.getPatterns().get(dto.getPatterns().size() - 1);
            signal = generate(latestPattern);
        }
        return signal;
    }

    private Signal generate(PatternDetectionResDto.Pattern pattern) {
        return switch (pattern.getPatternType().getDirection()) {
            case BULLISH -> Signal.BUY;
            case BEARISH -> Signal.SELL;
            default -> Signal.NONE;
        };
    }
}
