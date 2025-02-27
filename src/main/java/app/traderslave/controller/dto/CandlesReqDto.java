package app.traderslave.controller.dto;

import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.utility.TimeUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CandlesReqDto {

    @NotNull(message = "required")
    private CurrencyPair currencyPair;

    @NotNull(message = "required")
    private TimeFrame timeFrame;

    private LocalDateTime startDate = TimeUtils.now().minusSeconds(1);
    private LocalDateTime endDate = TimeUtils.now();
}
