package app.traderslave.exception.custom;

import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResDto;

public class CustomException extends BaseCustomException {

    public CustomException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum);
    }

    @Override
    protected ExceptionResDto buildCustomResponseDto() {
        return null;
    }
}
