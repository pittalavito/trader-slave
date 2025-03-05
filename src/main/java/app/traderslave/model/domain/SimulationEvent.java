package app.traderslave.model.domain;

import app.traderslave.model.enums.SOrderStatus;
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
@Table(name = "EVENT_SIMULATION")
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimulationEvent extends BasePersistentModel {

    @Column(nullable = false)
    private Long simulationId;

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
        LIQUIDATED_ORDER;

        public static EventType retrieveByOrderStatus(SOrderStatus status) {
            EventType eventType = null;
            switch (status) {
                case LIQUIDATED -> eventType = LIQUIDATED_ORDER;
                case CLOSED -> eventType = CLOSED_ORDER;
                case OPEN -> eventType = CREATED_ORDER;
            }
            return eventType;
        }
    }
}
