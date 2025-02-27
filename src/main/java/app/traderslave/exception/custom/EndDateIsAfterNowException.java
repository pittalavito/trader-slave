package app.traderslave.exception.custom;

import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResDto;

public class EndDateIsAfterNowException extends BaseCustomException {

    public EndDateIsAfterNowException() {
        super(ExceptionEnum.END_DATE_IS_AFTER_NOW);
    }

    @Override
    protected ExceptionResDto buildCustomResponseDto() {
        return null;
    }

}