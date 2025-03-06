package app.traderslave.controller.dto;

import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CandlesReqDto {

    @NotNull(message = "required")
    private CurrencyPair currencyPair;

    @NotNull(message = "required")
    private TimeFrame timeFrame;

    private boolean realTimeRequest = false;

    /**
     * Number of candles to retrieve. Used only if realTimeRequest is true.
     */
    private int lastNumCandle = 1000;

    /**
     * Start time for the candle data. Can be null only if realTimeRequest is true.
     */
    private LocalDateTime startTime;

    /**
     * End time for the candle data. Can be null only if realTimeRequest is true.
     */
    private LocalDateTime endTime;
}