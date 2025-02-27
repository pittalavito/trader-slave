package app.traderslave.exception.custom;

import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResDto;

public class StartDateIsAfterEndDateException extends BaseCustomException {

    public StartDateIsAfterEndDateException() {
        super(ExceptionEnum.START_DATE_IS_AFTER_END_DATE);
    }

    @Override
    protected ExceptionResDto buildCustomResponseDto() {
        return null;
    }

}