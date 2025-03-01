package app.traderslave.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum Currency {
    BTC("bitcoin", BigDecimal.valueOf(1d)),
    USD("american dollar", BigDecimal.valueOf(1000d)),
    USDT("tether american dollar", BigDecimal.valueOf(1000d));

    private final String name;
    private final BigDecimal defaultCapital;
}
