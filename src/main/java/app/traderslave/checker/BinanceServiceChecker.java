package app.traderslave.checker;

import app.traderslave.model.enums.TimeFrame;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class BinanceServiceChecker {

    public void checkDatesGetKline(TimeFrame timeFrame, LocalDateTime startDate, LocalDateTime endDate, Long limitNumCandles) {
        if (TimeUtils.areDatesNull(startDate, endDate)) {
            return;
        }
        TimeChecker.checkEndDate(endDate);
        TimeChecker.checkStartDate(startDate);
        TimeChecker.checkDateOrder(startDate, endDate);
        TimeChecker.checkCandleLimit(timeFrame, startDate, endDate, limitNumCandles);
    }
}

