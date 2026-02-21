package app.traderslave.bot.agent.impl.order_executor;

public abstract class BaseOrderExecutorAgent<T, R> {

    public abstract T execute(R request);
}
