package app.traderslave.model.report;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AiReportOrder {

    private StatusOrder status;
    private BigDecimal profitLoss;
    private BigDecimal profitLossMinusFees;
    private BigDecimal percentageChange;
    private BigDecimal maxUnrealizedProfitDuringTrade;
    private BigDecimal maxUnrealizedLossDuringTrade;
    private Long durationOfTradeInSeconds;

    public enum StatusOrder {
        OPEN_WITH_PROFIT,
        OPEN_WITH_LOSS,
        CLOSED_WITH_PROFIT,
        CLOSED_WITH_LOSS,
        LIQUIDATED
    }
}
