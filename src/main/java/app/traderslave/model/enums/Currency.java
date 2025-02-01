package app.traderslave.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    BTC("bitcoin"),
    USD("american dollar"),
    USDT("tether american dollar");

    private final String name;
}
