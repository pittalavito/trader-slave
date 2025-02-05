package app.traderslave.exception.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class ExceptionResponseDto {
    private HttpStatus httpStatus;
    private String message;
}
