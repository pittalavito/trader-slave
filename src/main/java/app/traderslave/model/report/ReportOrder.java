package app.traderslave.model.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportOrder {
    private BigDecimal closePrice;
    private LocalDateTime closeTime;
    private BigDecimal profitLoss;
    private BigDecimal profitLossMinusFees;
    private BigDecimal percentageChange;
    private BigDecimal maxUnrealizedProfitDuringTrade;
    private BigDecimal maxUnrealizedLossDuringTrade;
    private Long durationOfTradeInSeconds;
    @JsonIgnore
    private boolean liquidated;
}
