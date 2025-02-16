package app.traderslave.checker;

import app.traderslave.exception.custom.*;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class TimeChecker {

    public void validateEndDate(LocalDateTime endDate) {
        if (endDate != null && endDate.isAfter(TimeUtils.nowUTC())) {
            throw new EndDateIsAfterNowException();
        }
    }

    public void validateStartDate(LocalDateTime startDate) {
        if (startDate != null && startDate.isAfter(TimeUtils.nowUTC())) {
            throw new StartDateIsAfterNowException();
        }
    }

    public void validateDateOrder(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new StartDateIsAfterEndDateException();
        }
    }

    public void validateCandleLimit(TimeFrame timeFrame, LocalDateTime startDate, LocalDateTime endDate, long limitNumCandles) {
        if (startDate != null && endDate != null) {
            long diffMillis = TimeUtils.convertToUTCMillisecond(endDate) - TimeUtils.convertToUTCMillisecond(startDate);
            long numCandles = diffMillis / timeFrame.getMillisecond();
            if (limitNumCandles < numCandles) {
                throw new NumCandlesExceedsLimitException();
            }
        }
    }
}
