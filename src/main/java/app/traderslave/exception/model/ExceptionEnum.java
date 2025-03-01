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
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "Insufficient balance"),
    ORDER_STATUS_IS_NOT_OPEN(HttpStatus.BAD_REQUEST, "Order status is not open"),
    SIMULATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Simulation not fount"),
    SIMULATION_ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Simulation order not fount");

    private final HttpStatus httpStatus;
    private final String message;
}
