package app.traderslave.checker;

import app.traderslave.model.enums.TimeFrame;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class BinanceServiceChecker {

    public void checkDatesGetKline(TimeFrame timeFrame, LocalDateTime startDate, LocalDateTime endDate, int limitNumCandles) {
        TimeChecker.checkEndDate(endDate);
        TimeChecker.checkStartDate(startDate);
        TimeChecker.checkDateOrder(startDate, endDate);
        TimeChecker.checkCandleLimit(timeFrame, startDate, endDate, limitNumCandles);
    }
}

