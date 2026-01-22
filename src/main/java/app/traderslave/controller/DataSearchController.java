package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.service.BinanceCandleBackTestService;
import app.traderslave.service.BinanceService;
import app.traderslave.utility.ControllerPath;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(ControllerPath.DATA_SEARCH)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DataSearchController {

    private static final String URL_CANDLES = "/candles";
    private static final String URL_CANDLE = "/candle";
    private static final String URL_CANDLE_BACK_TEST = "/candles-back-test";

    private final BinanceService binanceService;
    private final BinanceCandleBackTestService binanceCandleBackTestService;

    @GetMapping(path = URL_CANDLE)
    public Mono<ResponseEntity<CandleResDto>> getCandle(@ModelAttribute @Validated CandleReqDto requestDto) {
        return binanceService.findCandle(requestDto)
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = URL_CANDLES)
    public Mono<ResponseEntity<CandlesResDto>> getCandles(@ModelAttribute @Validated CandlesReqDto requestDto) {
        return binanceService.findCandles(requestDto)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = URL_CANDLE_BACK_TEST)
    public ResponseEntity<Void> saveCandleBackTest(@ModelAttribute @Validated CandlesReqDto requestDto) {
        var response = binanceService.findCandles(requestDto).block();
        assert response != null;
        binanceCandleBackTestService.saveCandleBackTest(requestDto, response);
        return ResponseEntity.ok().build();
    }
}
