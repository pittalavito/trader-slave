package app.traderslave.utility;

import lombok.experimental.UtilityClass;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@UtilityClass
public class TimeUtils {

    public Long convertToUTCMillisecondOrDefault(LocalDateTime localDateTime, Long defaultValue) {
        return localDateTime == null ? defaultValue : convertToUTCMillisecond(localDateTime);
    }

    public Long convertToUTCMillisecond(LocalDateTime localDateTime) {
        return localDateTime
                .toEpochSecond(ZoneOffset.UTC) * 1000;
    }

    public LocalDateTime convertToUTCLocalDate(Long millisecond) {
        return Instant
                .ofEpochMilli(millisecond)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    public LocalDateTime nowUTC() {
        return LocalDateTime
                .now()
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime();
    }

}
