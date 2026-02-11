package app.traderslave.bot.service.market;

public abstract class BaseMarketDataService<T, R> {

    public abstract T get(R request);
}
