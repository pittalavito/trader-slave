package app.traderslave.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CurrencyPair {
    BTC_USDT(Currency.BTC, Currency.USDT),
    SOL_USDC(Currency.SOL, Currency.USDC);

    private final Currency numCurrency;
    private final Currency denCurrency;
}
