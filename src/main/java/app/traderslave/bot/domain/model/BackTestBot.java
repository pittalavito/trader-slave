package app.traderslave.bot.domain.model;

import app.traderslave.bot.BotConfig;
import app.traderslave.domain.model.BasePersistentModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Entity
@NoArgsConstructor
@Table(name = "BACK_TEST_BOT", indexes = {
        @Index(name = "bot_idx_simulation_id", columnList = "simulationId")
})
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BackTestBot extends BasePersistentModel {

    @Column(nullable = false)
    private Long portfolioId;

    private Status status;

    // todo: diventa un clob
    @Transient
    private BotConfig config;

    public enum Status {
        RUNNING,
        BLOCKED,
    }
}
