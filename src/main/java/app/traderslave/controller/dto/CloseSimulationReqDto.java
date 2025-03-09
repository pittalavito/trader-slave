package app.traderslave.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CloseSimulationReqDto extends TimeReqDto {
    @NotNull(message = "required")
    private Long simulationId;
}
