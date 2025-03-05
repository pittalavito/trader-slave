package app.traderslave.model.domain;

import app.traderslave.model.enums.OrderType;
import app.traderslave.model.enums.SOrderStatus;
import app.traderslave.utility.SqlColumnDefinition;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@Entity
@NoArgsConstructor
@Table(name = "SIMULATION_ORDER")
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimulationOrder extends BasePersistentModel {

    @Column(nullable = false)
    private Long simulationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.VARCHAR_20)
    private OrderType type;

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2_DEFAULT_0)
    private BigDecimal amountOfTrade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.VARCHAR_20)
    private SOrderStatus status;

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2_DEFAULT_0)
    private BigDecimal openPrice;

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime openTime;

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2_DEFAULT_0)
    private BigDecimal liquidationPrice;

    @Column(nullable = false)
    private Integer leverage;

    @Column(columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2)
    private BigDecimal closePrice;

    @Column(columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime closeTime;

    @Column(columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2)
    private BigDecimal profitLoss;

    @Column(columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2)
    private BigDecimal profitLossMinusFees;

    @Column(columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2)
    private BigDecimal percentageChange;

    @Column(name = "MU_PROFIT_DT", columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2)
    private BigDecimal maxUnrealizedProfitDuringTrade;

    @Column(name = "MU_LOSS_DT", columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2)
    private BigDecimal maxUnrealizedLossDuringTrade;

    @Column(name = "DU_TRADE_MILL", columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2)
    private Long durationOfTradeInSeconds;

    @Column(name = "MAX_PRICE_DT", columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2)
    private BigDecimal maxPriceDuringTrade;

    @Column(name = "MIN_PRICE_DT", columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2)
    private BigDecimal minPriceDuringTrade;
}
