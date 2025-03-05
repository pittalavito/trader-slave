package app.traderslave.model.report;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AiReportSimulation {
    private BigDecimal initialBalance;
    private BigDecimal finalRealizedBalance;
    private BigDecimal maxRealizedBalanceDuringSimulation;
    private BigDecimal minRealizedBalanceDuringSimulation;
    private BigDecimal finalBalanceIfOpenOrdersWereClosedNow;
    private BigDecimal balancePercentageChange;
    private BigDecimal balancePercentageChangeIfOpenOrdersWereClosedNow;
    private Long durationOfSimulationInSeconds;
    private Order openOrder;
    private Order closedOrder;
    private Order liquidatedOrder;

    @Data
    @Builder
    private static class Order {
        private Long numOrder;
        private Long numOrderInProfit;
        private Long numOrderInLoss;
        private BigDecimal summaryProfitLoss;
    }
}
