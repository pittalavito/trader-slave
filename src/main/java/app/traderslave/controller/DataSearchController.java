package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.service.BinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/data-search")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DataSearchController {

    private static final String URL_CANDLES = "/candles";
    private static final String URL_CANDLE = "/candle";

    private final BinanceService binanceService;

    @GetMapping(path = URL_CANDLE)
    public Mono<ResponseEntity<CandleResDto>> getCandle(@RequestParam CurrencyPair currencyPair, @RequestParam(required = false) LocalDateTime time) {
        return binanceService.findCandle(currencyPair, time)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = URL_CANDLES)
    public Mono<ResponseEntity<CandlesResDto>> getCandles(@RequestBody @Validated CandlesReqDto requestDto) {
        return binanceService.findCandles(requestDto)
                .map(ResponseEntity::ok);
    }

}
