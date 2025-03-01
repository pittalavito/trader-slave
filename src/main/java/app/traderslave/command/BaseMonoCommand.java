package app.traderslave.command;

import reactor.core.publisher.Mono;

/**
 * @param <T> HTTP REQUEST DTO
 * @param <R> HTTP RESPONSE DTO
 */
public abstract class BaseMonoCommand<T, R>{

    protected T requestDto;

    protected BaseMonoCommand(T requestDto) {
        this.requestDto = requestDto;
    }

    public abstract Mono<R> execute();
}
