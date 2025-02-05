package app.traderslave.exception.custom;

import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResponseDto;

public class NumCandlesExceedsLimitException extends BaseCustomException {

    public NumCandlesExceedsLimitException() {
        super(ExceptionEnum.NUM_CANDLES_EXCEEDS_LIMIT);
    }

    @Override
    protected ExceptionResponseDto buildCustomResponseDto() {
        return null;
    }

}