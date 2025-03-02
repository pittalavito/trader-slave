package app.traderslave.checker;

import app.traderslave.exception.custom.*;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class TimeChecker {

    public void checkEndDate(LocalDateTime endDate) {
        if (endDate == null) {
            throw new CustomException(ExceptionEnum.END_DATE_IS_REQUIRED);
        }
        if (endDate.isAfter(TimeUtils.now())) {
            throw new CustomException(ExceptionEnum.END_DATE_IS_AFTER_NOW);
        }
    }

    public void checkStartDate(LocalDateTime startDate) {
        if (startDate == null) {
            throw new CustomException(ExceptionEnum.START_DATE_IS_REQUIRED);
        }
        if (startDate.isAfter(TimeUtils.now())) {
            throw new CustomException(ExceptionEnum.START_DATE_IS_AFTER_NOW);
        }
    }

    public void checkDateOrder(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new CustomException(ExceptionEnum.START_DATE_IS_AFTER_END_DATE);
        }
    }

    public void checkCandleLimit(TimeFrame timeFrame, LocalDateTime startDate, LocalDateTime endDate, long limitNumCandles) {
        if (startDate != null && endDate != null) {
            long diffMillis = TimeUtils.convertToMillisecond(endDate) - TimeUtils.convertToMillisecond(startDate);
            long numCandles = diffMillis / timeFrame.getMillisecond();
            if (limitNumCandles < numCandles) {
                throw new CustomException(ExceptionEnum.NUM_CANDLES_EXCEEDS_LIMIT);
            }
        }
    }
}
