package app.traderslave.command;

/**
 * @param <D> HTTP REQUEST DTO
 * @param <R> HTTP RESPONSE DTO
 */
public abstract class BaseCommand <D, R> {

    protected D requestDto;

    public R execute(D requestDto) {
        this.requestDto = requestDto;
        return doExecute();
    }

    protected abstract R doExecute();
}