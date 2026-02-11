package app.traderslave.bot.service.risk;

public abstract class BaseRiskManagerService<T, R> {

    public abstract T calculate(R request);
}
