package app.traderslave.model.dto.req;

import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.model.enums.Currency;
import app.traderslave.model.enums.CurrencyPair;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BackTestCreatePortfolioReqDto {
    @NotNull(message = "required")
    private CurrencyPair currencyPair;
    private Currency currency = Currency.USD;
    private String description;
    private LocalDateTime startTime;
    private BackTestPortfolio.DataSource dataSource = BackTestPortfolio.DataSource.BINANCE_API;
}
