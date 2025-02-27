package app.traderslave.exception.custom;

import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResDto;

public class NumCandlesExceedsLimitException extends BaseCustomException {

    public NumCandlesExceedsLimitException() {
        super(ExceptionEnum.NUM_CANDLES_EXCEEDS_LIMIT);
    }

    @Override
    protected ExceptionResDto buildCustomResponseDto() {
        return null;
    }

}