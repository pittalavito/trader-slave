package app.traderslave.checker;

import app.traderslave.controller.dto.CandleReqDto;
import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BinanceServiceChecker {

    public static final int LIMIT_NUM_CANDLES = 50000;

    public void checkDatesGetKline(CandlesReqDto dto) {
        if (!dto.isRealTimeRequest()) {
            TimeChecker.checkEndDate(dto.getEndDate());
            TimeChecker.checkStartDate(dto.getStartDate());
            TimeChecker.checkDateOrder(dto.getStartDate(), dto.getEndDate());
            TimeChecker.checkCandleLimit(dto.getTimeFrame(), dto.getStartDate(), dto.getEndDate(), LIMIT_NUM_CANDLES);
            return;
        }
        if (dto.getLastNumCandle() <= 0 || dto.getLastNumCandle() > LIMIT_NUM_CANDLES) {
            throw new CustomException(ExceptionEnum.LAST_NUM_CANDLE_INVALID);
        }
    }

    public void checkDatesGetKline(CandleReqDto dto) {
        if (dto.isRealTimeRequest()) {
            return;
        }
        TimeChecker.checkStartDate(dto.getTime());
    }
}

