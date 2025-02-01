package app.traderslave.utility;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@UtilityClass
public class TimeUtils {

    public Long convertToUTCMillisecond(@NotNull LocalDateTime localDateTime) {
        return localDateTime.toEpochSecond(ZoneOffset.UTC) * 1000;
    }

    public LocalDateTime convertToUTCLocalDate(@NotNull Long millisecond) {
        return Instant
                .ofEpochMilli(millisecond)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
    }
}
