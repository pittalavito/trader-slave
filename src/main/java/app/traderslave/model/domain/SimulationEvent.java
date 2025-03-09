package app.traderslave.model.domain;

import app.traderslave.utility.SqlColumnDefinition;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@Entity
@NoArgsConstructor
@Table(name = "EVENT_SIMULATION", indexes = {
        @Index(name = "event_idx_simulation_id", columnList = "simulationId"),
        @Index(name = "event_idx_order_id", columnList = "orderId")
})
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimulationEvent extends BasePersistentModel {

    @Column(nullable = false)
    private Long simulationId;

    @Column(nullable = false)
    private Long orderId;

    @Column(columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime eventTime;

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2_DEFAULT_0)
    private BigDecimal balanceUpdates;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.VARCHAR_20)
    private EventType eventType;

    public enum EventType {
        CREATED_ORDER,
        CLOSED_ORDER,
        CLOSED_SIMULATION;

        public static EventType retrieveByOrderStatus(SimulationOrder.Status status) {
            EventType eventType = null;
            if(status == SimulationOrder.Status.OPEN) {
                eventType = CREATED_ORDER;
            } else {
                eventType = CLOSED_ORDER;
            }
            return eventType;
        }
    }
}
