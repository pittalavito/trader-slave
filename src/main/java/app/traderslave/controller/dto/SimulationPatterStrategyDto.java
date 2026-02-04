package app.traderslave.controller.dto;

import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class SimulationPatterStrategyDto extends TimeReqDto {

    private CurrencyPair currencyPair = CurrencyPair.SOL_USDC;
    private LocalDateTime simulationStartTime = LocalDateTime.of(2025, 1, 1, 0, 0);
    private LocalDateTime simulationEndTime = LocalDateTime.of(2025, 1, 8, 0, 0);

    private TimeFrame patternDetectionTimeFrame = TimeFrame.FIVE_MINUTES;
    private TimeFrame emaAnalysisTimeFrame = TimeFrame.FIFTEEN_MINUTES;

    private BigDecimal amountForTradePercentage = BigDecimal.valueOf(0.05);
    private BigDecimal takeProfitPercentage = BigDecimal.valueOf(0.0275);
    private BigDecimal stopLossPercentage = BigDecimal.valueOf(0.0175);

    private int candleInterval = 72;
    private int leverage = 20;
}
