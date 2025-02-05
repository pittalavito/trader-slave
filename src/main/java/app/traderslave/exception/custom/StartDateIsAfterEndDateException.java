package app.traderslave.exception.custom;

import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResponseDto;

public class StartDateIsAfterEndDateException extends BaseCustomException {

    public StartDateIsAfterEndDateException() {
        super(ExceptionEnum.START_DATE_IS_AFTER_END_DATE);
    }

    @Override
    protected ExceptionResponseDto buildCustomResponseDto() {
        return null;
    }

}