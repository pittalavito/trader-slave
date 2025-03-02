package app.traderslave.controller.dto;

import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.service.BinanceService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CandlesReqDto {

    @NotNull(message = "required")
    private CurrencyPair currencyPair;

    @NotNull(message = "required")
    private TimeFrame timeFrame;

    private boolean realTimeCandles = false;

    @Size(min = 1 , max = BinanceService.LIMIT_NUM_CANDLES, message = "the value must be between 1 and " + BinanceService.LIMIT_NUM_CANDLES)
    private int lastNumCandle = 1000;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
