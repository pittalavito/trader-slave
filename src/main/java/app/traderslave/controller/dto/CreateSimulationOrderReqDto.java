package app.traderslave.controller.dto;

import app.traderslave.model.enums.OrderType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateSimulationOrderReqDto {

    @NotNull(message = "required")
    private Long simulationId;
    @NotNull(message = "required")
    private OrderType orderType;
    @Size(min = 1, max = 100)
    private int leverage = 1;
    private boolean maxAmountOfTrade = false;
    private boolean realTimeRequest = false;
    private BigDecimal amountOfTrade;
    private LocalDateTime time;
}
