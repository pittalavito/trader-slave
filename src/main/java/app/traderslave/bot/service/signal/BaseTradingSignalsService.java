package app.traderslave.bot.service.signal;

public abstract class BaseTradingSignalsService<T, R> {

    public abstract T generate(R request);

}
