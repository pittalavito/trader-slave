package app.traderslave.utility;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@UtilityClass
public class TimeUtils {

    public long convertToMillisecond(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public LocalDateTime convertToLocalDateTime(Long millisecond) {
        return Instant.ofEpochMilli(millisecond)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    public LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public boolean areDatesNull(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate == null && endDate == null;
    }

    public LocalDateTime getOrDefaultValue(LocalDateTime time, @NotNull LocalDateTime defaultValue) {
        return time == null ? defaultValue : time;
    }

    public LocalDateTime getOrDefaultValueMinusSecs(LocalDateTime time, @NotNull LocalDateTime defaultValue, int seconds) {
        return time == null ? defaultValue.minusSeconds(1) : time.minusSeconds(seconds);
    }

}
