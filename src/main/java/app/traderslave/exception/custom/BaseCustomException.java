package app.traderslave.exception.custom;

import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.exception.model.ExceptionResDto;
import lombok.Getter;
import java.util.Objects;

@Getter
public abstract class BaseCustomException extends RuntimeException {

    private final transient ExceptionResDto body;

    protected BaseCustomException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.body = buildDtoResponse(exceptionEnum);
    }

    protected abstract ExceptionResDto buildCustomResponseDto();

    private ExceptionResDto buildDtoResponse(ExceptionEnum exceptionEnum) {
        ExceptionResDto response = buildCustomResponseDto();
        response = Objects.isNull(response) ? new ExceptionResDto() : response;
        response.setMessage(exceptionEnum.getMessage());
        response.setHttpStatus(exceptionEnum.getHttpStatus());
        return response;
    }
}
