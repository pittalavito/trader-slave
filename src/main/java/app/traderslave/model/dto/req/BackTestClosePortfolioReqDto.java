package app.traderslave.model.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BackTestClosePortfolioReqDto extends TimeReqDto {
    @NotNull(message = "required")
    private Long simulationId;
}
