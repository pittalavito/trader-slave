package app.traderslave.controller.dto;

import app.traderslave.model.enums.Currency;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PostSimulationResDto {
    private Long simulationId;
    private Currency currency;
    private BigDecimal balance;
}
