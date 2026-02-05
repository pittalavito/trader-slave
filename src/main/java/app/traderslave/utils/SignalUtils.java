package app.traderslave.utils;

import app.traderslave.controller.dto.PatternDetectionResDto;
import app.traderslave.model.enums.OrderType;
import app.traderslave.model.enums.PatternType;
import app.traderslave.model.enums.Signal;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

@UtilityClass
public class SignalUtils {

    public Signal generate(PatternDetectionResDto dto) {
        Signal signal;
        if (CollectionUtils.isEmpty(dto.getPatterns())) {
            signal = Signal.NONE;
        } else {
            PatternDetectionResDto.Pattern latestPattern = dto.getPatterns().get(dto.getPatterns().size() - 1);
            signal = generate(latestPattern);
        }
        return signal;
    }

    public boolean confirmOrderSignal(OrderType orderType, PatternDetectionResDto dto) {
        if (CollectionUtils.isEmpty(dto.getPatterns())) {
            return false;
        }
        PatternDetectionResDto.Pattern latestPattern = dto.getPatterns().get(dto.getPatterns().size() - 1);
        Signal signal = generate(latestPattern);
        return switch (signal) {
            case BUY -> orderType == OrderType.BUY;
            case SELL -> orderType == OrderType.SELL;
            default -> PatternType.Direction.BOTH == latestPattern.getPatternType().getDirection();
        };
    }

    private Signal generate(PatternDetectionResDto.Pattern pattern) {
        return switch (pattern.getPatternType().getDirection()) {
            case BULLISH -> Signal.BUY;
            case BEARISH -> Signal.SELL;
            default -> Signal.NONE;
        };
    }
}
