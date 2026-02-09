package app.traderslave.bot.base;

public abstract class RiskManager <T, R> {

    public abstract T calculate(R request);
}
