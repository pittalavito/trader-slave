package app.traderslave.bot.base;

public abstract class PositionSizer <T, R> {

    public abstract T calculate(R request);

}
