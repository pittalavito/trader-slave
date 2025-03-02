package app.traderslave.controller.dto;

import app.traderslave.model.enums.CurrencyPair;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CandleReqDto {

    @NotNull(message = "required")
    private CurrencyPair currencyPair;

    private boolean realTimeCandle = false;

    private LocalDateTime time;
}
