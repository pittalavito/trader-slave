package app.traderslave.utils;

import app.traderslave.model.dto.PatternDetectionDto;
import app.traderslave.model.enums.OrderType;
import app.traderslave.model.enums.PatternType;
import app.traderslave.model.enums.Signal;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

@UtilityClass
public class SignalUtils {

    public Signal generate(PatternDetectionDto dto) {
        Signal signal;
        if (CollectionUtils.isEmpty(dto.getPatterns())) {
            signal = Signal.NONE;
        } else {
            PatternDetectionDto.Pattern latestPattern = dto.getPatterns().get(dto.getPatterns().size() - 1);
            signal = generate(latestPattern);
        }
        return signal;
    }

    public boolean confirmOrderSignal(OrderType orderType, PatternDetectionDto dto) {
        if (CollectionUtils.isEmpty(dto.getPatterns())) {
            return false;
        }
        PatternDetectionDto.Pattern latestPattern = dto.getPatterns().get(dto.getPatterns().size() - 1);
        Signal signal = generate(latestPattern);
        return switch (signal) {
            case BUY -> orderType == OrderType.BUY;
            case SELL -> orderType == OrderType.SELL;
            default -> PatternType.Direction.BOTH == latestPattern.getPatternType().getDirection();
        };
    }

    private Signal generate(PatternDetectionDto.Pattern pattern) {
        return switch (pattern.getPatternType().getDirection()) {
            case BULLISH -> Signal.BUY;
            case BEARISH -> Signal.SELL;
            default -> Signal.NONE;
        };
    }
}
