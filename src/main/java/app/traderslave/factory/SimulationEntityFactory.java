package app.traderslave.factory;

import app.traderslave.model.domain.Simulation;
import app.traderslave.model.enums.Currency;
import app.traderslave.model.enums.CurrencyPair;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class SimulationEntityFactory {

    private final BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(100);
    private final Currency DEFAULT_CURRENCY = Currency.USDT;

    public Simulation create(CurrencyPair currencyPair) {
        return Simulation.builder()
                .currencyPair(currencyPair)
                .currency(DEFAULT_CURRENCY)
                .balance(DEFAULT_BALANCE)
                .uid(UUID.randomUUID().toString())
                .version(0)
                .build();
    }
}
