package app.traderslave.command.base;

import lombok.Setter;
import reactor.core.publisher.Mono;

/**
 * @param <T> HTTP REQUEST DTO
 * @param <R> HTTP RESPONSE DTO
 */
@Setter
public abstract class BaseMonoCommand<T, R>{

    protected T commandRequest;

    public abstract Mono<R> execute() throws InterruptedException;
}
