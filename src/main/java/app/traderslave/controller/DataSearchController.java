package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.exception.custom.*;
import app.traderslave.service.BinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/data-search")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DataSearchController {

    private static final String URL_CANDLES = "/candles";
    private static final String URL_CANDLE = "/candle";

    private final BinanceService binanceService;

    /**
     * Retrieves candle data based on the provided request parameters.
     *
     * @param requestDto the request DTO containing the parameters for the candle data search
     *                   - `currencyPair`: The currency pair for which the candle data is requested. This field is mandatory.
     *                   - `time`: The specific time for which the candle data is requested. If not provided, it defaults to the current time.
     * @return a Mono emitting a ResponseEntity containing the CandleResDto with the candle data
     * @throws IllegalArgumentException if the `currencyPair` is null or invalid
     * @throws StartDateIsAfterNowException if time date is after now
     * @throws BinanceRemoteException if remote service throw error
     */
    @GetMapping(path = URL_CANDLE)
    public Mono<ResponseEntity<CandleResDto>> getCandle(@ModelAttribute @Validated CandleReqDto requestDto) {
        return binanceService.findCandle(requestDto)
                .map(ResponseEntity::ok);
    }

    /**
     * Retrieves multiple candles data based on the provided request parameters.
     *
     * @param requestDto the request DTO containing the parameters for the candles data search
     *                   - `currencyPair`: The currency pair for which the candles data is requested. This field is mandatory.
     *                   - `startTime`: The start time for the range of candles data requested. If not provided, it defaults to the current time minus 1 second
     *                   - `endTime`: The end time for the range of candles data requested. If not provided, it defaults to the current time.
     *                   - `timeFrame`: example "ONE_MINUTES". This field is mandatory.
     * @return a Mono emitting a ResponseEntity containing the CandlesResDto with the candles data
     * @throws IllegalArgumentException if the `currencyPair or timeFrame` is null or invalid
     * @throws StartDateIsAfterNowException if start date is after now
     * @throws EndDateIsAfterNowException if end date is after now
     * @throws StartDateIsAfterEndDateException if start date is after end date
     * @throws NumCandlesExceedsLimitException if num candle requested > 50000
     * @throws BinanceRemoteException if remote service throw error
     */
    @GetMapping(path = URL_CANDLES)
    public Mono<ResponseEntity<CandlesResDto>> getCandles(@ModelAttribute @Validated CandlesReqDto requestDto) {
        return binanceService.findCandles(requestDto)
                .map(ResponseEntity::ok);
    }

}
