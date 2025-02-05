package app.traderslave.exception;

import app.traderslave.exception.custom.BaseCustomException;
import app.traderslave.exception.model.ExceptionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvice {

    /**
     * Is used to handle error descriptions on {@link Object} fields, when the fields are annotated with any {@link jakarta.validation.constraints},
     * and the Object is validated with {@link org.springframework.validation.annotation.Validated}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
        Map<String, String> body =  e.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        field ->  ((FieldError) field).getField(),
                        ObjectError::getDefaultMessage)
                );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(BaseCustomException.class)
    public ResponseEntity<ExceptionResponseDto> handleCustomException(BaseCustomException e, WebRequest request) {
        return new ResponseEntity<>(e.getBody(), e.getBody().getHttpStatus());
    }

}
