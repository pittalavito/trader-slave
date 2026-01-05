package app.traderslave.service;

import app.traderslave.assembler.JupiterPerpetualCsvAssembler;
import app.traderslave.assembler.PatternDetectionAssembler;
import app.traderslave.controller.dto.*;
import app.traderslave.model.Pattern;
import app.traderslave.utility.CsvUtils;
import app.traderslave.utility.PatternUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
                .map(candles -> detectPatterns(candles.getList(), dto.getLookBack(), dto.getTolerancePercent(), dto.getMinDistance()))
                .map(patterns -> PatternDetectionAssembler.toModel(patterns, dto));
    }

    /**
    * Detects the most common patterns in the candles.
    *
    * @param candles List of OHLC candles
    * @param lookBack Number of candles to identify local maxima/minima
    * @param tolerancePercent Maximum difference between maxima/minima for similar patterns
    * @param minDistance Minimum number of candles between two maxima/minima to consider the pattern valid
    * @return List of detected patterns
    */
    private List<Pattern> detectPatterns(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<Pattern> detectedPatterns = new ArrayList<>();

        detectedPatterns.addAll(PatternUtils.detectDoubleTop(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectDoubleBottom(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectHeadAndShoulders(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectInverseHeadAndShoulders(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectTripleTop(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectTripleBottom(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectRoundingTop(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectRoundingBottom(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectAscendingTriangle(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectDescendingTriangle(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectSymmetricalTriangle(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectFlag(candles, lookBack));
        detectedPatterns.addAll(PatternUtils.detectPennant(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectRisingWedge(candles, lookBack, minDistance));
        detectedPatterns.addAll(PatternUtils.detectFallingWedge(candles, lookBack, minDistance));
        detectedPatterns.addAll(PatternUtils.detectRectangle(candles, lookBack, tolerancePercent));

        detectedPatterns.forEach(pattern -> pattern.setBreakoutConfirmed(PatternUtils.isBreakoutConfirmed(pattern, candles)));

        return detectedPatterns;
    }
}
