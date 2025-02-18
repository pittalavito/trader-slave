package app.traderslave.model.domain;

import app.traderslave.model.enums.Currency;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.utility.SqlColumnDefinition;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "SIMULATION")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Simulation extends BasePersistentModel {

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime startDate;

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.VARCHAR_20)
    private CurrencyPair currencyPair;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.VARCHAR_20)
    private Currency currency;

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.BIG_DECIMAL_30_2_DEFAULT_0)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.VARCHAR_20)
    private TimeFrame timeFrame;

}
