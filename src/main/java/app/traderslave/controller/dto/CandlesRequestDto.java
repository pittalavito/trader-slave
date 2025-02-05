package app.traderslave.controller.dto;

import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CandlesRequestDto {
    @NotNull
    private CurrencyPair currencyPair = CurrencyPair.BTC_USDT;
    @NotNull(message = "required")
    private LocalDateTime startDate;
    @NotNull(message = "required")
    private LocalDateTime endDate;
    @NotNull(message = "required")
    private TimeFrame timeFrame;
}
