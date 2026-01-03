package app.traderslave.controller;

import app.traderslave.assembler.JupiterPerpetualCsvAssembler;
import app.traderslave.assembler.PatternDetectionAssembler;
import app.traderslave.controller.dto.*;
import app.traderslave.service.PatternDetectionService;
import app.traderslave.utility.ControllerPath;
import app.traderslave.utility.CsvUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping(ControllerPath.TRADE_ANALYSES)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TradeAnalysesController {

    private static final String URI_JUPITER_CSV = "/jupiter-perpetual-csv";
    private static final String URI_PATTERN_DETECTION = "/pattern-detection";

    private final PatternDetectionService patternDetectionService;

    @PostMapping(path = URI_JUPITER_CSV)
    public ResponseEntity<JupiterPerpetualCsvResDto> createFromJupiterPerpetualCsv(@RequestParam("file") MultipartFile file) {
        var listTrades = CsvUtils.readCsvFile(file, JupiterPerpetualCsvReqDto.class);
        var resDto = JupiterPerpetualCsvAssembler.toModel(listTrades);
        return ResponseEntity.ok(resDto);
    }

    @PostMapping(path = URI_PATTERN_DETECTION)
    public ResponseEntity<Mono<PatternDetectionResDto>> detectPatterns(@RequestBody PatternDetectionReqDto dto) {
        var body = patternDetectionService.detect(dto).map(patterns -> PatternDetectionAssembler.toModel(patterns, dto));
        return ResponseEntity.ok(body);
    }
}
