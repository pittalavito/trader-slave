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

    public void validateDatesGetKline(TimeFrame timeFrame, LocalDateTime startDate, LocalDateTime endDate, Long limitNumCandles) {
        if (startDate == null && endDate == null) {
            return;
        }
        final LocalDateTime now = TimeUtils.nowUTC();
        if (startDate == null) {
            if (endDate.isAfter(now)) {
                throw new EndDateIsAfterNowException();
            }
        } else if (endDate == null) {
            if(startDate.isAfter(now)) {
                throw new StartDateIsAfterNowException();
            }
        } else if (startDate.isAfter(endDate)) {
            throw new StartDateIsAfterEndDateException();
        } else if (limitNumCandles < ((TimeUtils.convertToUTCMillisecond(startDate) - TimeUtils.convertToUTCMillisecond(endDate)) / timeFrame.getMillisecond())) {
            throw new NumCandlesExceedsLimitException();
        }
    }

}
