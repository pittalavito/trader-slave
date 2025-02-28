package app.traderslave.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    END_DATE_IS_AFTER_NOW(HttpStatus.BAD_REQUEST, "The end date is after now"),
    NUM_CANDLES_EXCEEDS_LIMIT(HttpStatus.BAD_REQUEST, "The number of candles exceeds the allowed limit"),
    START_DATE_IS_AFTER_END_DATE(HttpStatus.BAD_REQUEST, "The start date is after end date"),
    START_DATE_IS_AFTER_NOW(HttpStatus.BAD_REQUEST, "The start date is after now"),
    BINANCE_REMOTE(HttpStatus.INTERNAL_SERVER_ERROR, null),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity not found"),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "Insufficient balance");

    private final HttpStatus httpStatus;
    private final String message;
}
