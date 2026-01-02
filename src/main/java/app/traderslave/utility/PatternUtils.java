package app.traderslave.utility;

import app.traderslave.model.Candle;
import app.traderslave.model.Pattern;
import app.traderslave.model.enums.PatternType;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@UtilityClass
public class PatternUtils {

    /**
     * Finds the local maxima in a list of candles using Streams.
     *
     * @param candles  List of OHLC candles
     * @param lookBack Number of candles before and after to consider
     * @return List of candles that are local maxima
     * @throws IllegalArgumentException if the input list is null, empty, or lookBack is invalid
     */
    public List<Candle> findLocalMaxima(List<Candle> candles, int lookBack) {
        validateInput(candles, lookBack);
        return IntStream.range(lookBack, candles.size() - lookBack)
                .filter(i -> isLocalMaximum(candles, i, lookBack))
                .mapToObj(candles::get)
                .toList();
    }

    /**
     * Finds the local minima in a list of candles using Streams.
     *
     * @param candles  List of OHLC candles
     * @param lookBack Number of candles before and after to consider
     * @return List of candles that are local minima
     * @throws IllegalArgumentException if the input list is null, empty, or lookBack is invalid
     */
    public List<Candle> findLocalMinima(List<Candle> candles, int lookBack) {
        validateInput(candles, lookBack);
        return IntStream.range(lookBack, candles.size() - lookBack)
                .filter(i -> isLocalMinimum(candles, i, lookBack))
                .mapToObj(candles::get)
                .toList();
    }

    public List<Pattern> detectDoubleTop(List<Candle> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<Candle> maxima = findLocalMaxima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < maxima.size() - 1; i++) {
            Candle first = maxima.get(i);
            Candle second = maxima.get(i + 1);
            int firstIndex = candles.indexOf(first);
            int secondIndex = candles.indexOf(second);

            if (secondIndex - firstIndex < minDistance) continue;

            double diffPercent = calculateDifferencePercent(first.getHigh().doubleValue(), second.getHigh().doubleValue());

            if (diffPercent <= tolerancePercent) {
                candles.subList(firstIndex, secondIndex + 1)
                        .stream()
                        .min(Comparator.comparingDouble(c -> c.getLow().doubleValue()))
                        .filter(minBetween -> minBetween.getLow().doubleValue() < first.getHigh().doubleValue())
                        .ifPresent(minBetween -> patternsFound.add(new Pattern(PatternType.DOUBLE_TOP, List.of(first, minBetween, second), false)));
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectDoubleBottom(List<Candle> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<Candle> minima = findLocalMinima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < minima.size() - 1; i++) {
            Candle first = minima.get(i);
            Candle second = minima.get(i + 1);
            int firstIndex = candles.indexOf(first);
            int secondIndex = candles.indexOf(second);

            if (secondIndex - firstIndex < minDistance) continue;

            double diffPercent = calculateDifferencePercent(first.getLow().doubleValue(), second.getLow().doubleValue());

            if (diffPercent <= tolerancePercent) {
                candles.subList(firstIndex, secondIndex + 1)
                        .stream()
                        .min(Comparator.comparingDouble(c -> c.getLow().doubleValue()))
                        .filter(maxBetween -> maxBetween.getHigh().doubleValue() > first.getLow().doubleValue())
                        .ifPresent(maxBetween ->patternsFound.add(new Pattern(PatternType.DOUBLE_BOTTOM, List.of(first, maxBetween, second), false)));
            }
        }

        return patternsFound;
    }

    // --- Private Helper Methods ---

    private void validateInput(List<Candle> candles, int lookBack) {
        if (candles == null || candles.isEmpty()) {
            throw new IllegalArgumentException("The candle list cannot be null or empty.");
        }
        if (lookBack <= 0) {
            throw new IllegalArgumentException("LookBack must be greater than 0.");
        }
        if (candles.size() <= 2 * lookBack) {
            throw new IllegalArgumentException("The candle list is too small for the given lookBack.");
        }
    }

    private boolean isLocalMaximum(List<Candle> candles, int index, int lookBack) {
        return IntStream.rangeClosed(index - lookBack, index + lookBack)
                .filter(j -> j != index)
                .allMatch(j -> candles.get(index).getHigh().doubleValue() > candles.get(j).getHigh().doubleValue());
    }

    private boolean isLocalMinimum(List<Candle> candles, int index, int lookBack) {
        return IntStream.rangeClosed(index - lookBack, index + lookBack)
                .filter(j -> j != index)
                .allMatch(j -> candles.get(index).getLow().doubleValue() < candles.get(j).getLow().doubleValue());
    }

    private double calculateDifferencePercent(double value1, double value2) {
        return Math.abs(value1 - value2) / value1;
    }
}