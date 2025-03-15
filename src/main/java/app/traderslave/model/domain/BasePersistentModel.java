package app.traderslave.model.domain;

import app.traderslave.utility.SqlColumnDefinition;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class BasePersistentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = SqlColumnDefinition.VARCHAR_UID)
    private String uid;

    @Column(columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(columnDefinition = SqlColumnDefinition.TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP)
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = SqlColumnDefinition.INTEGER_DEFAULT_0)
    private Integer version;
}
