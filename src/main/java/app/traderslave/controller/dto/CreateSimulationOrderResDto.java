package app.traderslave.controller.dto;

import app.traderslave.model.enums.OrderType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateSimulationOrderResDto {
    private Long id;
    private Long simulationId;
    private OrderType orderType;
    private BigDecimal amountOfTrade;
    private BigDecimal openPrice;
    private LocalDateTime openTime;
    private BigDecimal balance;
}
