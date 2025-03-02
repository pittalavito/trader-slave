package app.traderslave.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CloseSimulationOrderReqDto {

    @NotNull(message = "required")
    private Long simulationId;
    private boolean realTimeRequest = false;
    private LocalDateTime time;
}
