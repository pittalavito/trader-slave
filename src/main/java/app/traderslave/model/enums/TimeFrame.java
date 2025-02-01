package app.traderslave.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TimeFrame {
    ONE_MINUTE("1m", 60000L),
    THREE_MINUTES("3m", 3 * ONE_MINUTE.millisecond),
    FIVE_MINUTES("5m", 5 * ONE_MINUTE.millisecond),
    FIFTEEN_MINUTES("15m", 15 * ONE_MINUTE.millisecond),
    THIRTY_MINUTES("30m", 30 * ONE_MINUTE.millisecond),
    ONE_HOUR("1h", 60 * ONE_MINUTE.millisecond),
    TWO_HOURS("2h", 2 * ONE_HOUR.millisecond),
    FOUR_HOURS("4h", 4 * ONE_HOUR.millisecond),
    SIX_HOURS("6h", 6 * ONE_HOUR.millisecond),
    EIGHT_HOURS("8h", 8 * ONE_HOUR.millisecond),
    TWELVE_HOURS("12h", 12 * ONE_HOUR.millisecond),
    ONE_DAY("1d", 24 * ONE_HOUR.millisecond);

    @Getter
    private final String code;
    private final Long millisecond;

    public static TimeFrame fromCode(String code) {
        return Arrays.stream(TimeFrame.values())
                .filter(timeFrame -> timeFrame.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid TimeFrame code: " + code));
    }
}
