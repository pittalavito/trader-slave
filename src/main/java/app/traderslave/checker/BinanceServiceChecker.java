package app.traderslave.checker;

import app.traderslave.controller.dto.CandleReqDto;
import app.traderslave.controller.dto.CandlesReqDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BinanceServiceChecker {

    public static final int LIMIT_NUM_CANDLES = 50000;

    public void checkDatesGetKline(CandlesReqDto dto) {
        if (dto.isRealTimeRequest()) {
            return;
        }
        TimeChecker.checkEndDate(dto.getEndDate());
        TimeChecker.checkStartDate(dto.getStartDate());
        TimeChecker.checkDateOrder(dto.getStartDate(), dto.getEndDate());
        TimeChecker.checkCandleLimit(dto.getTimeFrame(), dto.getStartDate(), dto.getEndDate(), LIMIT_NUM_CANDLES);
    }

    public void checkDatesGetKline(CandleReqDto dto) {
        if (dto.isRealTimeRequest()) {
            return;
        }
        TimeChecker.checkStartDate(dto.getTime());
    }
}

