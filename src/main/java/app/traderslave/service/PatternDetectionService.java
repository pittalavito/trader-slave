package app.traderslave.service;

import app.traderslave.model.Candle;
import app.traderslave.model.Pattern;
import app.traderslave.utility.PatternUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PatternDetectionService {

  /**
   * Detects the most common patterns in the candles.
   *
   * @param candles List of OHLC candles
   * @param lookBack Number of candles to identify local maxima/minima
   * @param tolerancePercent Maximum difference between maxima/minima for similar patterns
   * @param minDistance Minimum number of candles between two maxima/minima to consider the pattern valid
   * @return List of detected patterns
   */
    public List<Pattern> detectPatterns(List<Candle> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<Pattern> detectedPatterns = new ArrayList<>();

        detectedPatterns.addAll(PatternUtils.detectDoubleTop(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(PatternUtils.detectDoubleBottom(candles, lookBack, tolerancePercent, minDistance));

        return detectedPatterns;
    }
}
