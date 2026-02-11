package app.traderslave.bot.service.position;

public abstract class BasePositionSizerService<T, R> {

    public abstract T calculate(R request);

}
