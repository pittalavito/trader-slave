package app.traderslave.controller.dto;

import app.traderslave.model.enums.SOrderStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CloseSimulationOrderResDto {
    private Long simulationId;
    private Long orderId;
    private SOrderStatus status;
    private BigDecimal balance;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Integer leverage;
    private BigDecimal amountOfTrade;
    private BigDecimal profitLoss;
    private BigDecimal profitLossMinusFees;
    private BigDecimal percentageChange;
    private BigDecimal maxUnrealizedProfitDuringTrade;
    private BigDecimal maxUnrealizedLossDuringTrade;
    private Long durationOfTradeInSeconds;
}
