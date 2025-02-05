package app.traderslave.exception.custom;

import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResponseDto;

public class StartDateIsAfterNowException extends BaseCustomException {

    public StartDateIsAfterNowException() {
        super(ExceptionEnum.START_DATE_IS_AFTER_NOW);
    }

    @Override
    protected ExceptionResponseDto buildCustomResponseDto() {
        return null;
    }

}