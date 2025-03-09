package app.traderslave.controller.dto;

import app.traderslave.model.enums.Currency;
import app.traderslave.model.enums.CurrencyPair;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateSimulationReqDto {
    @NotNull(message = "required")
    private CurrencyPair currencyPair;
    private Currency currency = Currency.USD;
    private String description;
    private LocalDateTime startTime;
}
