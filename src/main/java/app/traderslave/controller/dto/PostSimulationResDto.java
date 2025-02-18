package app.traderslave.controller.dto;

import app.traderslave.model.enums.Currency;
import app.traderslave.model.enums.TimeFrame;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PostSimulationResDto {
    private Long simulationId;
    private Currency currency;
    private BigDecimal balance;
    private CandlesResDto candles;
    private TimeFrame timeFrame;
}
