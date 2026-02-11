package app.traderslave.bot.service.executor;

public abstract class BaseOrderExecutorService<T, R> {

    public abstract T execute(R request);
}
