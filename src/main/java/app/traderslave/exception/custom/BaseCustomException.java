package app.traderslave.exception.custom;

import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResponseDto;
import lombok.Getter;
import java.util.Objects;

@Getter
public abstract class BaseCustomException extends RuntimeException {

    private final transient ExceptionResponseDto body;

    protected BaseCustomException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.body = buildDtoResponse(exceptionEnum);
    }

    protected abstract ExceptionResponseDto buildCustomResponseDto();

    private ExceptionResponseDto buildDtoResponse(ExceptionEnum exceptionEnum) {
        ExceptionResponseDto response = buildCustomResponseDto();
        response = Objects.isNull(response) ? new ExceptionResponseDto() : response;
        response.setMessage(exceptionEnum.getMessage());
        response.setHttpStatus(exceptionEnum.getHttpStatus());
        return response;
    }
}
