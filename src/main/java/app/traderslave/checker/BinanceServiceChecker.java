package app.traderslave.checker;

import app.traderslave.model.enums.TimeFrame;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class BinanceServiceChecker {

    public void validateDatesGetKline(TimeFrame timeFrame, LocalDateTime startDate, LocalDateTime endDate, Long limitNumCandles) {
        if (TimeUtils.areDatesNull(startDate, endDate)) {
            return;
        }
        TimeChecker.validateEndDate(endDate);
        TimeChecker.validateStartDate(startDate);
        TimeChecker.validateDateOrder(startDate, endDate);
        TimeChecker.validateCandleLimit(timeFrame, startDate, endDate, limitNumCandles);
    }
}

