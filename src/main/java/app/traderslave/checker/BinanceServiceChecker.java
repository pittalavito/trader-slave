package app.traderslave.checker;

import app.traderslave.controller.dto.CandleReqDto;
import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class BinanceServiceChecker {

    public static final int LIMIT_NUM_CANDLES = 50000;

    public void checkDatesGetKline(CandlesReqDto dto) {
        if (!dto.isRealTimeRequest()) {
            TimeChecker.checkEndDate(dto.getEndTime());
            TimeChecker.checkStartDate(dto.getStartTime());
            TimeChecker.checkDates(dto.getStartTime(), dto.getEndTime());
            checkCandleLimit(dto.getTimeFrame(), dto.getStartTime(), dto.getEndTime());
        } else if (dto.getLastNumCandle() <= 0 || dto.getLastNumCandle() > LIMIT_NUM_CANDLES) {
            throw new CustomException(ExceptionEnum.LAST_NUM_CANDLE_INVALID);
        }
    }

    public void checkDatesGetKline(CandleReqDto dto) {
        if (dto.isRealTimeRequest()) {
            return;
        }
        TimeChecker.checkStartDate(dto.getStartTime());
    }

    private void checkCandleLimit(TimeFrame timeFrame, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            long diffMillis = TimeUtils.convertToMillisecond(endDate) - TimeUtils.convertToMillisecond(startDate);
            long numCandles = diffMillis / timeFrame.getMillisecond();
            if (LIMIT_NUM_CANDLES < numCandles) {
                throw new CustomException(ExceptionEnum.NUM_CANDLES_EXCEEDS_LIMIT);
            }
        }
    }
}

