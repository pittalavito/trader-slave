package app.traderslave.checker;

import app.traderslave.exception.custom.EndDateIsAfterNowException;
import app.traderslave.exception.custom.NumCandlesExceedsLimitException;
import app.traderslave.exception.custom.StartDateIsAfterEndDateException;
import app.traderslave.exception.custom.StartDateIsAfterNowException;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class BinanceServiceChecker {

    public void validateDatesGetKline(TimeFrame timeFrame, Long startDateMillisecond, Long endDateMillisecond, Long limitNumCandles) {

        if (startDateMillisecond != null && endDateMillisecond != null) {

            final Long nowMillisecond = TimeUtils.convertToUTCMillisecond(LocalDateTime.now());

            if (endDateMillisecond > nowMillisecond) {
                throw new EndDateIsAfterNowException();
            }
            if (startDateMillisecond > nowMillisecond) {
                throw new StartDateIsAfterNowException();
            }
            if (startDateMillisecond > endDateMillisecond) {
                throw new StartDateIsAfterEndDateException();
            }
            if (limitNumCandles < ((startDateMillisecond - endDateMillisecond) / timeFrame.getMillisecond())) {
                throw new NumCandlesExceedsLimitException();
            }
        }
    }

}
