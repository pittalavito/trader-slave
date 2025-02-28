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

    /**
     * Price vs currency simulation
     */
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2_DEFAULT_0)
    private BigDecimal openPrice;

    /**
     * Price vs currency simulation
     */
    @Column(columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2_DEFAULT_0)
    private BigDecimal closePrice;

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime openTime;

    @Column(columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime closeTime;

    //todo è un json per informaioni aggiuntive
    private String extraInfo;
}
