package app.traderslave.controller;

import app.traderslave.domain.service.CandleBackTestDomainService;
import app.traderslave.model.dto.req.CandleReqDto;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.model.dto.req.JupiterPerpetualCsvReqDto;
import app.traderslave.model.dto.req.PatternDetectionReqDto;
import app.traderslave.model.dto.res.JupiterPerpetualCsvResDto;
import app.traderslave.model.dto.res.PatternDetectionResDto;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.remote.service.BinanceRemoteService;
import app.traderslave.assembler.JupiterPerpetualCsvAssembler;
import app.traderslave.assembler.PatternDetectionAssembler;
import app.traderslave.utils.ControllerPath;
import app.traderslave.utils.CsvUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(ControllerPath.TRADE_ANALYSES)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DataAnalysesController {

    private static final String URI_JUPITER_CSV = "/jupiter-perpetual-csv";
    private static final String URI_PATTERN_DETECTION = "/pattern-detection";
    private static final String URL_CANDLES = "/candles";
    private static final String URL_CANDLE = "/candle";
    private static final String URL_CANDLE_BACK_TEST = "/candles-back-test";

    private final BinanceRemoteService binanceRemoteService;
    private final CandleBackTestDomainService candleBackTestDomainService;

    @GetMapping(path = URL_CANDLE)
    public Mono<ResponseEntity<CandleDto>> getCandle(@ModelAttribute @Validated CandleReqDto requestDto) {
        return binanceRemoteService.findCandleAsync(requestDto)
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = URL_CANDLES)
    public Mono<ResponseEntity<List<CandleDto>>> getCandles(@ModelAttribute @Validated CandlesReqDto requestDto) {
        return binanceRemoteService.findCandlesAsync(requestDto)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = URL_CANDLE_BACK_TEST)
    public ResponseEntity<Void> saveCandleBackTest(@ModelAttribute @Validated CandlesReqDto requestDto) {
        binanceRemoteService.findCandlesAsync(requestDto).doOnNext(response ->
                candleBackTestDomainService.saveCandleBackTest(requestDto, response)
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = URI_JUPITER_CSV)
    public ResponseEntity<JupiterPerpetualCsvResDto> createFromJupiterPerpetualCsv(@RequestParam("file") MultipartFile file) {
        List<JupiterPerpetualCsvReqDto> listTrades = CsvUtils.readCsvFile(file, JupiterPerpetualCsvReqDto.class);
        return ResponseEntity.ok(JupiterPerpetualCsvAssembler.toModel(listTrades));
    }

    @PostMapping(path = URI_PATTERN_DETECTION)
    public ResponseEntity<Mono<PatternDetectionResDto>> detectPatterns(@RequestBody PatternDetectionReqDto dto) {
        return ResponseEntity.ok(binanceRemoteService.findCandlesAsync(dto)
                .map(candles -> PatternDetectionAssembler.toModel(candles, dto))
        );
    }
}
