package app.traderslave.controller.dto;

import app.traderslave.model.enums.OrderType;
import app.traderslave.utility.TimeUtils;
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

    @Size(min = 10)
    private BigDecimal amountOfTrade;

    private boolean maxAmountOfTrade = amountOfTrade == null;

    private LocalDateTime time = TimeUtils.now();
}
