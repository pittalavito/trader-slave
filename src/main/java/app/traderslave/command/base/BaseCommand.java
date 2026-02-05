package app.traderslave.command.base;

import lombok.Setter;

/**
 * @param <T> HTTP REQUEST DTO
 * @param <R> HTTP RESPONSE DTO
 */
@Setter
public abstract class BaseCommand<T, R>{

    protected T commandRequest;

    public abstract R execute() throws InterruptedException;
}
