package app.traderslave.bot.agent.impl.position;

public abstract class BasePositionSizerAgent<T, R> {

    public abstract T calculate(R request);

}
