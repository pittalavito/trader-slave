package app.traderslave.controller.dto;

import app.traderslave.model.enums.OrderType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateSimulationOrderReqDto extends TimeReqDto {

    @NotNull(message = "required")
    private Long simulationId;
    @NotNull(message = "required")
    private OrderType orderType;
    @Size(min = 1, max = 100)
    private int leverage = 1;
    private boolean maxAmountOfTrade = false;
    private BigDecimal amountOfTrade;
}
