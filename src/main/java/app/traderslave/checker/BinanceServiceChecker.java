package app.traderslave.checker;

import app.traderslave.model.enums.TimeFrame;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class BinanceServiceChecker {

    public void validateDatesGetKline(TimeFrame timeFrame, Long startDateMillisecond, Long endDateMillisecond, Long limitNumCandles) {

        if (startDateMillisecond != null && endDateMillisecond != null) {
            //todo creare le eccezioni personalizzate
            final Long nowMillisecond = TimeUtils.convertToUTCMillisecond(LocalDateTime.now());

            if (endDateMillisecond > nowMillisecond) {
                throw new RuntimeException("endDate > now");
            }
            if (startDateMillisecond > nowMillisecond) {
                throw new RuntimeException("stardDate > now");
            }
            if (startDateMillisecond < endDateMillisecond) {
                throw new RuntimeException("startDate cannot be less than endDate");
            }
            if (limitNumCandles < ((startDateMillisecond - endDateMillisecond) / timeFrame.getMillisecond())) {
                throw new RuntimeException("The number of candles exceeds the allowed limit");
            }
        }
    }

}
