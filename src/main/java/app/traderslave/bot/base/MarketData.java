package app.traderslave.bot.base;

public abstract class MarketData <T, R> {

    public abstract T get(R request);
}
