package app.traderslave.exception.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class EntityNotFoundResDto extends ExceptionResDto {
    private String className;
}
