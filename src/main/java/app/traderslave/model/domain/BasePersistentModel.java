package app.traderslave.model.domain;

import app.traderslave.utility.SqlColumnDefinition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Setter
@Getter
@SuperBuilder
@MappedSuperclass
public abstract class BasePersistentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = SqlColumnDefinition.VARCHAR_UID)
    private String uid;

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime creationDate;

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime lastModificationDate;

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.INTEGER_DEFAULT_0)
    private Integer version;
}
