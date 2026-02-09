package app.traderslave.bot.base;

public abstract class OrderExecutor <T, R> {

    public abstract T execute(R request);
}
