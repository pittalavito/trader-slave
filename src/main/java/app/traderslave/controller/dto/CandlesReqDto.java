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

    private int lastNumCandle = 1000;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
