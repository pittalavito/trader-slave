package app.traderslave.controller.dto;

import app.traderslave.model.domain.SimulationEvent;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CloseSimulationResDto {
    private String requestInfo;
    private Long simulationId;
    private List<SimulationOrderResDto> openOrders;
    private List<SimulationOrderResDto> closeOrders;
    private List<SimulationOrderResDto> liquidateOrders;
    private List<BalanceEvent> balanceEvents;

    private BigDecimal initialBalance;
    private BigDecimal finalRealizedBalance;
    private BigDecimal balancePercentageChange;
    private BigDecimal maxRealizedBalanceDuringSimulation;
    private BigDecimal minRealizedBalanceDuringSimulation;

    private BigDecimal finalBalanceIfOpenOrdersWereClosedNow;
    private BigDecimal balancePercentageChangeIfOpenOrdersWereClosedNow;

    private Long numOrders;
    private Long numOrdersInProfit;
    private Long numOrdersInLoss;

    private BigDecimal percentageOrderInProfit;

    private Long durationOfSimulationInSeconds;

    @Data
    @Builder
    public static class BalanceEvent {
        private LocalDateTime time;
        private SimulationEvent.EventType type;
        private BigDecimal value;

    }
}
