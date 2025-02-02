package app.traderslave.controller;

import app.traderslave.controller.dto.*;
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

    private final BinanceService binanceService;

    @PostMapping(path = URL_CANDLES)
    public Mono<ResponseEntity<CandlesResponseDto>> getCandles(@RequestBody @Validated CandlesRequestDto requestDto) {
        return binanceService.getCandleSticks(requestDto)
                .map(ResponseEntity::ok);
    }
}
