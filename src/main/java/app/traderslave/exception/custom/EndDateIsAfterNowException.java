package app.traderslave.exception.custom;

import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResponseDto;

public class EndDateIsAfterNowException extends BaseCustomException {

    public EndDateIsAfterNowException() {
        super(ExceptionEnum.END_DATE_IS_AFTER_NOW);
    }

    @Override
    protected ExceptionResponseDto buildCustomResponseDto() {
        return null;
    }

}