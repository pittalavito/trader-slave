package app.traderslave.bot.agent.impl.risk;

public abstract class BaseRiskAgent<T, R> {

    public abstract T calculate(R request);
}
