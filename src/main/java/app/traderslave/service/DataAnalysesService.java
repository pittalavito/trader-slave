package app.traderslave.service;

import app.traderslave.assembler.JupiterPerpetualCsvAssembler;
import app.traderslave.assembler.PatternDetectionAssembler;
import app.traderslave.controller.dto.*;
import app.traderslave.utility.CsvUtils;
import app.traderslave.utility.PatternUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DataAnalysesService {

    private final BinanceService binanceService;

    public JupiterPerpetualCsvResDto createFromJupiterPerpetualCsv(MultipartFile file) {
        List<JupiterPerpetualCsvReqDto> listTrades = CsvUtils.readCsvFile(file, JupiterPerpetualCsvReqDto.class);
        return JupiterPerpetualCsvAssembler.toModel(listTrades);
    }

    public Mono<PatternDetectionResDto> detectPatterns(PatternDetectionReqDto dto) {
        return binanceService.findCandles(dto)
                .map(candles -> PatternUtils.detectPatterns(candles.getList(), dto.getLookBack(), dto.getTolerancePercent(), dto.getMinDistance()))
                .map(patterns -> PatternDetectionAssembler.toModel(patterns, dto));
    }

}
