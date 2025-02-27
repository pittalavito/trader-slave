package app.traderslave.exception.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class ExceptionResDto {
    private HttpStatus httpStatus;
    private String message;
}
