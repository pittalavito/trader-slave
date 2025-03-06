package app.traderslave.controller.dto;

import app.traderslave.model.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimulationOrderResDto {
    private String requestInfo;
    private Long simulationId;
    private Long orderId;
    private OrderType orderType;
    private Status status;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Integer leverage;
    private BigDecimal amountOfTrade;
    private BigDecimal liquidationPrice;
    private BigDecimal profitLoss;
    private BigDecimal profitLossMinusFees;
    private BigDecimal percentageChange;
    private BigDecimal maxUnrealizedProfitDuringTrade;
    private BigDecimal maxUnrealizedLossDuringTrade;
    private Long durationOfTradeInSeconds;
    private BigDecimal maxPriceDuringTrade;
    private BigDecimal minPriceDuringTrade;

    public enum Status {
        OPEN_NOW,
        OPEN_NEUTRAL,
        OPEN_WITH_PROFIT,
        OPEN_WITH_LOSS,
        CLOSED_NEUTRAL,
        CLOSED_WITH_PROFIT,
        CLOSED_WITH_LOSS,
        LIQUIDATED;
    }
}
