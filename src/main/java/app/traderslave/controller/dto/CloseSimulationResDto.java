package app.traderslave.controller.dto;

import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloseSimulationResDto {
    private Long simulationId;
    private Map<Long, SimulationOrderResDto> ordersIdsMap;
    private Map<SimulationOrderResDto.Status, List<Long>> ordersIdsStatusMap;
    private List<Event> events;

    private BigDecimal initialBalance;
    private BigDecimal finalBalance;
    private BigDecimal balancePercentageChange;

    private BigDecimal maxUnrealizedBalance;
    private BigDecimal minUnrealizedBalance;

    private Integer numOrders;
    private Integer numOrdersInProfit;
    private Integer numOrdersInLoss;
    private Integer numOrderLiquidated;
    private Integer numOrderOpen;
    private BigDecimal percentageOrderProfit;
    private BigDecimal percentageNumOrderLoss;
    private BigDecimal percentageNumOrderLiquidated;
    private BigDecimal percentageNumOrderOpen;

    private Long durationOfSimulationInSeconds;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Event {
        private LocalDateTime time;
        private Long orderId;
        private SimulationEvent.EventType eventType;
        private BigDecimal balanceUpdate;
        private OrderType orderType;
        private BigDecimal openPrice;
        private BigDecimal closePrice;
        private LocalDateTime openTime;
        private LocalDateTime closeTime;
        private Integer leverage;
        private BigDecimal amountOfTrade;
        private BigDecimal percentageChange;
        private Boolean isProfit;
    }
}
