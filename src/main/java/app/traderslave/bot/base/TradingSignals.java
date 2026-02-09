package app.traderslave.bot.base;

public abstract class TradingSignals <T, R> {

    public abstract T generate(R request);

}
