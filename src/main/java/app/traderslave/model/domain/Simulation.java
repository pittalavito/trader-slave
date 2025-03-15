package app.traderslave.model.domain;

import app.traderslave.model.enums.Currency;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.utility.SqlColumnDefinition;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@Entity
@NoArgsConstructor
@Table(name = "SIMULATION")
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Simulation extends BasePersistentModel {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.VARCHAR_20)
    private CurrencyPair currencyPair;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.VARCHAR_20)
    private Currency currency;

    @Positive
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2_DEFAULT_0)
    private BigDecimal balance;

    @Column(columnDefinition = SqlColumnDefinition.VARCHAR_255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.VARCHAR_20)
    private Status status;

    @Column(columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime startTime;

    @Column(columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime endTime;

    //todo [Crilin] Version 1.0
    //@Lob pensare di salvare solo il report finale
    //@Column(columnDefinition = "CLOB")
    //private String finalReport;

    public boolean isOpen() {
        return Status.OPEN == status;
    }

    public enum Status {
        OPEN,
        CLOSED
    }

}
