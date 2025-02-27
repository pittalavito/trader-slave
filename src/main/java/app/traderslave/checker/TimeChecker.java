package app.traderslave.checker;

import app.traderslave.exception.custom.*;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class TimeChecker {

    public void checkEndDate(LocalDateTime endDate) {
        if (endDate != null && endDate.isAfter(TimeUtils.now())) {
            throw new EndDateIsAfterNowException();
        }
    }

    public void checkStartDate(LocalDateTime startDate) {
        if (startDate != null && startDate.isAfter(TimeUtils.now())) {
            throw new StartDateIsAfterNowException();
        }
    }

    public void checkDateOrder(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new StartDateIsAfterEndDateException();
        }
    }

    public void checkCandleLimit(TimeFrame timeFrame, LocalDateTime startDate, LocalDateTime endDate, long limitNumCandles) {
        if (startDate != null && endDate != null) {
            long diffMillis = TimeUtils.convertToMillisecond(endDate) - TimeUtils.convertToMillisecond(startDate);
            long numCandles = diffMillis / timeFrame.getMillisecond();
            if (limitNumCandles < numCandles) {
                throw new NumCandlesExceedsLimitException();
            }
        }
    }
}
