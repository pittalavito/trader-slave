package app.traderslave.model.persistence;

import app.traderslave.utility.SqlColumnDefinition;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
@MappedSuperclass
public abstract class BasePersistentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, columnDefinition = SqlColumnDefinition.VARCHAR_UID)
    private String uid;

    @NotNull
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDate creationDate;

    @NotNull
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDate lastModificationDate;

    @NotNull
    @Column(nullable = false, columnDefinition = SqlColumnDefinition.INTEGER_DEFAULT_0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer version;
}
