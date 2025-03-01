package app.traderslave.controller.dto;

import app.traderslave.utility.TimeUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CloseSimulationOrderReqDto {

    @NotNull(message = "required")
    private Long simulationId;

    private LocalDateTime time = TimeUtils.now();
}
