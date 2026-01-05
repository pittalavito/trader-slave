package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.service.DataAnalysesService;
import app.traderslave.utility.ControllerPath;
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
public class DataAnalysesController {

    private static final String URI_JUPITER_CSV = "/jupiter-perpetual-csv";
    private static final String URI_PATTERN_DETECTION = "/pattern-detection";

    private final DataAnalysesService dataAnalysesService;

    @PostMapping(path = URI_JUPITER_CSV)
    public ResponseEntity<JupiterPerpetualCsvResDto> createFromJupiterPerpetualCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(dataAnalysesService.createFromJupiterPerpetualCsv(file));
    }

    @PostMapping(path = URI_PATTERN_DETECTION)
    public ResponseEntity<Mono<PatternDetectionResDto>> detectPatterns(@RequestBody PatternDetectionReqDto dto) {
        return ResponseEntity.ok(dataAnalysesService.detectPatterns(dto));
    }
}
