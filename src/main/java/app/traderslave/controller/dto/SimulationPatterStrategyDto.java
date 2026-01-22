package app.traderslave.controller.dto;

import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class SimulationPatterStrategyDto extends TimeReqDto {

    private static final int CANDLES_INTERVAL = 72;

    private static final int LEVERAGE = 20;

    private static final TimeFrame TIME_FRAME = TimeFrame.FIVE_MINUTES;
    private static final CurrencyPair CURRENCY_PAIR = CurrencyPair.SOL_USDC;
    private static final BigDecimal PERCENTAGE_OF_BALANCE_PER_TRADE = BigDecimal.valueOf(0.05);

    private static final BigDecimal TAKE_PROFIT_PERCENTAGE = BigDecimal.valueOf(0.03);
    private static final BigDecimal STOP_LOSS_PERCENTAGE = BigDecimal.valueOf(0.03);

    private static final LocalDateTime SIMULATION_START_TIME = LocalDateTime.of(2026, 1, 1, 0, 0);
    private static final LocalDateTime SIMULATION_END_TIME = LocalDateTime.of(2026, 1, 20, 0, 0);
}
