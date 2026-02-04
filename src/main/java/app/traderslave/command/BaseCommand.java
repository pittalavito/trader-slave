package app.traderslave.command;

import lombok.Setter;

/**
 * @param <T> HTTP REQUEST DTO
 * @param <R> HTTP RESPONSE DTO
 */
@Setter
public abstract class BaseCommand<T, R>{

    protected T requestDto;

    public abstract R execute() throws InterruptedException;
}
