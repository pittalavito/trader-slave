package app.traderslave.exception.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EntityNotFoundResDto extends ExceptionResDto {
    private String className;
}
