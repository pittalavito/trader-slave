package app.traderslave.utility;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.PatternDetectionReqDto;
import app.traderslave.model.Pattern;
import app.traderslave.model.enums.PatternType;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@UtilityClass
public class PatternUtils {


    public List<Pattern> detectPatterns(List<CandleResDto> candles, PatternDetectionReqDto dto) {
        return detectPatterns(candles, dto.getLookBack(), dto.getTolerancePercent(), dto.getMinDistance());
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
    public List<Pattern> detectPatterns(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<Pattern> detectedPatterns = new ArrayList<>();

        detectedPatterns.addAll(detectDoubleTop(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectDoubleBottom(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectHeadAndShoulders(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectInverseHeadAndShoulders(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectTripleTop(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectTripleBottom(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectRoundingTop(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectRoundingBottom(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectAscendingTriangle(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectDescendingTriangle(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectSymmetricalTriangle(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectFlag(candles, lookBack));
        detectedPatterns.addAll(detectPennant(candles, lookBack, tolerancePercent, minDistance));
        detectedPatterns.addAll(detectRisingWedge(candles, lookBack, minDistance));
        detectedPatterns.addAll(detectFallingWedge(candles, lookBack, minDistance));
        detectedPatterns.addAll(detectRectangle(candles, lookBack, tolerancePercent));

        detectedPatterns.forEach(pattern -> pattern.setBreakoutConfirmed(isBreakoutConfirmed(pattern, candles)));

        return detectedPatterns;
    }

    public List<Pattern> detectDoubleTop(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> maxima = findLocalMaxima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < maxima.size() - 1; i++) {
            CandleResDto first = maxima.get(i);
            CandleResDto second = maxima.get(i + 1);
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

    public List<Pattern> detectDoubleBottom(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> minima = findLocalMinima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < minima.size() - 1; i++) {
            CandleResDto first = minima.get(i);
            CandleResDto second = minima.get(i + 1);
            int firstIndex = candles.indexOf(first);
            int secondIndex = candles.indexOf(second);

            if (secondIndex - firstIndex < minDistance) continue;

            double diffPercent = calculateDifferencePercent(first.getLow().doubleValue(), second.getLow().doubleValue());

            if (diffPercent <= tolerancePercent) {
                candles.subList(firstIndex, secondIndex + 1)
                        .stream()
                        .min(Comparator.comparingDouble(c -> c.getLow().doubleValue()))
                        .filter(maxBetween -> maxBetween.getHigh().doubleValue() > first.getLow().doubleValue())
                        .ifPresent(maxBetween -> patternsFound.add(new Pattern(PatternType.DOUBLE_BOTTOM, List.of(first, maxBetween, second), false)));
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectHeadAndShoulders(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> maxima = findLocalMaxima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < maxima.size() - 2; i++) {
            CandleResDto leftShoulder = maxima.get(i);
            CandleResDto head = maxima.get(i + 1);
            CandleResDto rightShoulder = maxima.get(i + 2);

            int idxLeft = candles.indexOf(leftShoulder);
            int idxHead = candles.indexOf(head);
            int idxRight = candles.indexOf(rightShoulder);

            boolean firstCondition = idxHead - idxLeft < minDistance || idxRight - idxHead < minDistance;
            boolean secondCondition = !(head.getHigh().doubleValue() > leftShoulder.getHigh().doubleValue() && head.getHigh().doubleValue() > rightShoulder.getHigh().doubleValue());

            if (firstCondition || secondCondition) continue;

            double diffShoulders = calculateDifferencePercent(leftShoulder.getHigh().doubleValue(), rightShoulder.getHigh().doubleValue());

            if (diffShoulders <= tolerancePercent) {

                CandleResDto minLeftHead = candles.subList(idxLeft, idxHead + 1)
                        .stream()
                        .min(Comparator.comparingDouble(c -> c.getLow().doubleValue()))
                        .orElse(null);

                CandleResDto minHeadRight = candles.subList(idxHead, idxRight + 1)
                        .stream()
                        .min(Comparator.comparingDouble(c -> c.getLow().doubleValue()))
                        .orElse(null);

                if (minLeftHead != null && minHeadRight != null) {
                    patternsFound.add(new Pattern(PatternType.HEAD_SHOULDERS, List.of(leftShoulder, minLeftHead, head, minHeadRight, rightShoulder), false));
                }
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectInverseHeadAndShoulders(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> minima = findLocalMinima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < minima.size() - 2; i++) {
            CandleResDto leftShoulder = minima.get(i);
            CandleResDto head = minima.get(i + 1);
            CandleResDto rightShoulder = minima.get(i + 2);

            int idxLeft = candles.indexOf(leftShoulder);
            int idxHead = candles.indexOf(head);
            int idxRight = candles.indexOf(rightShoulder);

            boolean firstCondition = idxHead - idxLeft < minDistance || idxRight - idxHead < minDistance;
            boolean secondCondition = !(head.getLow().doubleValue() < leftShoulder.getLow().doubleValue() && head.getLow().doubleValue() < rightShoulder.getLow().doubleValue());

            if (firstCondition || secondCondition) continue;

            double diffShoulders = calculateDifferencePercent(leftShoulder.getLow().doubleValue(), rightShoulder.getLow().doubleValue());

            if (diffShoulders <= tolerancePercent) {

                CandleResDto maxLeftHead = candles.subList(idxLeft, idxHead + 1)
                        .stream()
                        .max(Comparator.comparingDouble(c -> c.getHigh().doubleValue()))
                        .orElse(null);

                CandleResDto maxHeadRight = candles.subList(idxHead, idxRight + 1)
                        .stream()
                        .max(Comparator.comparingDouble(c -> c.getHigh().doubleValue()))
                        .orElse(null);

                if (maxLeftHead != null && maxHeadRight != null) {
                    patternsFound.add(new Pattern(PatternType.INVERSE_HEAD_SHOULDERS, List.of(leftShoulder, maxLeftHead, head, maxHeadRight, rightShoulder), false));
                }
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectTripleTop(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> maxima = findLocalMaxima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < maxima.size() - 2; i++) {
            CandleResDto first = maxima.get(i);
            CandleResDto second = maxima.get(i + 1);
            CandleResDto third = maxima.get(i + 2);

            int firstIndex = candles.indexOf(first);
            int secondIndex = candles.indexOf(second);
            int thirdIndex = candles.indexOf(third);

            if (secondIndex - firstIndex < minDistance || thirdIndex - secondIndex < minDistance) continue;

            double diffFirstSecond = calculateDifferencePercent(first.getHigh().doubleValue(), second.getHigh().doubleValue());
            double diffSecondThird = calculateDifferencePercent(second.getHigh().doubleValue(), third.getHigh().doubleValue());

            if (diffFirstSecond <= tolerancePercent && diffSecondThird <= tolerancePercent) {
                candles.subList(firstIndex, thirdIndex + 1)
                        .stream()
                        .min(Comparator.comparingDouble(c -> c.getLow().doubleValue()))
                        .filter(minBetween -> minBetween.getLow().doubleValue() < first.getHigh().doubleValue())
                        .ifPresent(minBetween -> patternsFound.add(new Pattern(PatternType.TRIPLE_TOP, List.of(first, second, third, minBetween), false)));
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectTripleBottom(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> minima = findLocalMinima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < minima.size() - 2; i++) {
            CandleResDto first = minima.get(i);
            CandleResDto second = minima.get(i + 1);
            CandleResDto third = minima.get(i + 2);

            int firstIndex = candles.indexOf(first);
            int secondIndex = candles.indexOf(second);
            int thirdIndex = candles.indexOf(third);

            if (secondIndex - firstIndex < minDistance || thirdIndex - secondIndex < minDistance) continue;

            double diffFirstSecond = calculateDifferencePercent(first.getLow().doubleValue(), second.getLow().doubleValue());
            double diffSecondThird = calculateDifferencePercent(second.getLow().doubleValue(), third.getLow().doubleValue());

            if (diffFirstSecond <= tolerancePercent && diffSecondThird <= tolerancePercent) {
                candles.subList(firstIndex, thirdIndex + 1)
                        .stream()
                        .max(Comparator.comparingDouble(c -> c.getHigh().doubleValue()))
                        .filter(maxBetween -> maxBetween.getHigh().doubleValue() > first.getLow().doubleValue())
                        .ifPresent(maxBetween -> patternsFound.add(new Pattern(PatternType.TRIPLE_BOTTOM, List.of(first, second, third, maxBetween), false)));
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectRoundingTop(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> maxima = findLocalMaxima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < maxima.size() - 2; i++) {
            CandleResDto first = maxima.get(i);
            CandleResDto middle = maxima.get(i + 1);
            CandleResDto last = maxima.get(i + 2);

            int firstIndex = candles.indexOf(first);
            int middleIndex = candles.indexOf(middle);
            int lastIndex = candles.indexOf(last);

            if (middleIndex - firstIndex < minDistance || lastIndex - middleIndex < minDistance) continue;

            boolean isRoundingTop = middle.getHigh().doubleValue() > first.getHigh().doubleValue()
                    && middle.getHigh().doubleValue() > last.getHigh().doubleValue();

            double diffFirstLast = calculateDifferencePercent(first.getHigh().doubleValue(), last.getHigh().doubleValue());

            if (isRoundingTop && diffFirstLast <= tolerancePercent) {
                patternsFound.add(new Pattern(PatternType.ROUNDING_TOP, List.of(first, middle, last), false));
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectRoundingBottom(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> minima = findLocalMinima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < minima.size() - 2; i++) {
            CandleResDto first = minima.get(i);
            CandleResDto middle = minima.get(i + 1);
            CandleResDto last = minima.get(i + 2);

            int firstIndex = candles.indexOf(first);
            int middleIndex = candles.indexOf(middle);
            int lastIndex = candles.indexOf(last);

            if (middleIndex - firstIndex < minDistance || lastIndex - middleIndex < minDistance) continue;

            boolean isRoundingBottom = middle.getLow().doubleValue() < first.getLow().doubleValue()
                    && middle.getLow().doubleValue() < last.getLow().doubleValue();

            double diffFirstLast = calculateDifferencePercent(first.getLow().doubleValue(), last.getLow().doubleValue());

            if (isRoundingBottom && diffFirstLast <= tolerancePercent) {
                patternsFound.add(new Pattern(PatternType.ROUNDING_BOTTOM, List.of(first, middle, last), false));
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectAscendingTriangle(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> minima = findLocalMinima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < minima.size() - 1; i++) {
            CandleResDto first = minima.get(i);
            CandleResDto second = minima.get(i + 1);
            int firstIndex = candles.indexOf(first);
            int secondIndex = candles.indexOf(second);

            if (secondIndex - firstIndex < minDistance) continue;

            double diffPercent = calculateDifferencePercent(first.getLow().doubleValue(), second.getLow().doubleValue());

            if (diffPercent <= tolerancePercent) {
                boolean isAscending = IntStream.range(firstIndex, secondIndex)
                        .allMatch(j -> candles.get(j).getHigh().doubleValue() <= candles.get(j + 1).getHigh().doubleValue());

                if (isAscending) {
                    patternsFound.add(new Pattern(PatternType.ASCENDING_TRIANGLE, candles.subList(firstIndex, secondIndex + 1), false));
                }
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectDescendingTriangle(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> maxima = findLocalMaxima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < maxima.size() - 1; i++) {
            CandleResDto first = maxima.get(i);
            CandleResDto second = maxima.get(i + 1);
            int firstIndex = candles.indexOf(first);
            int secondIndex = candles.indexOf(second);

            if (secondIndex - firstIndex < minDistance) continue;

            double diffPercent = calculateDifferencePercent(first.getHigh().doubleValue(), second.getHigh().doubleValue());

            if (diffPercent <= tolerancePercent) {
                boolean isDescending = IntStream.range(firstIndex, secondIndex)
                        .allMatch(j -> candles.get(j).getLow().doubleValue() >= candles.get(j + 1).getLow().doubleValue());

                if (isDescending) {
                    patternsFound.add(new Pattern(PatternType.DESCENDING_TRIANGLE, candles.subList(firstIndex, secondIndex + 1), false));
                }
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectSymmetricalTriangle(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> maxima = findLocalMaxima(candles, lookBack);
        List<CandleResDto> minima = findLocalMinima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < Math.min(maxima.size(), minima.size()) - 1; i++) {
            CandleResDto high1 = maxima.get(i);
            CandleResDto high2 = maxima.get(i + 1);
            CandleResDto low1 = minima.get(i);
            CandleResDto low2 = minima.get(i + 1);

            int highIndex1 = candles.indexOf(high1);
            int highIndex2 = candles.indexOf(high2);
            int lowIndex1 = candles.indexOf(low1);
            int lowIndex2 = candles.indexOf(low2);

            if (Math.abs(highIndex1 - lowIndex1) > minDistance && Math.abs(highIndex2 - lowIndex2) > minDistance) {
                double highDiff = calculateDifferencePercent(high1.getHigh().doubleValue(), high2.getHigh().doubleValue());
                double lowDiff = calculateDifferencePercent(low1.getLow().doubleValue(), low2.getLow().doubleValue());

                if (highDiff <= tolerancePercent && lowDiff <= tolerancePercent) {
                    patternsFound.add(new Pattern(PatternType.SYMMETRICAL_TRIANGLE, candles.subList(Math.min(highIndex1, lowIndex1), Math.max(highIndex2, lowIndex2) + 1), false));
                }
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectFlag(List<CandleResDto> candles, int lookBack) {
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < candles.size() - lookBack; i++) {
            List<CandleResDto> subList = candles.subList(i, i + lookBack);

            boolean isFlag = IntStream.range(1, subList.size() - 1)
                    .allMatch(j -> subList.get(j).getHigh().doubleValue() < subList.get(j - 1).getHigh().doubleValue()
                            && subList.get(j).getLow().doubleValue() > subList.get(j - 1).getLow().doubleValue());

            if (isFlag) {
                patternsFound.add(new Pattern(PatternType.FLAG, new ArrayList<>(subList), false));
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectPennant(List<CandleResDto> candles, int lookBack, double tolerancePercent, int minDistance) {
        List<CandleResDto> maxima = findLocalMaxima(candles, lookBack);
        List<CandleResDto> minima = findLocalMinima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < Math.min(maxima.size(), minima.size()) - 1; i++) {
            CandleResDto high1 = maxima.get(i);
            CandleResDto high2 = maxima.get(i + 1);
            CandleResDto low1 = minima.get(i);
            CandleResDto low2 = minima.get(i + 1);

            int highIndex1 = candles.indexOf(high1);
            int highIndex2 = candles.indexOf(high2);
            int lowIndex1 = candles.indexOf(low1);
            int lowIndex2 = candles.indexOf(low2);

            if (Math.abs(highIndex1 - lowIndex1) > minDistance && Math.abs(highIndex2 - lowIndex2) > minDistance) {
                double highDiff = calculateDifferencePercent(high1.getHigh().doubleValue(), high2.getHigh().doubleValue());
                double lowDiff = calculateDifferencePercent(low1.getLow().doubleValue(), low2.getLow().doubleValue());

                if (highDiff <= tolerancePercent && lowDiff <= tolerancePercent) {
                    patternsFound.add(new Pattern(PatternType.PENNANT, candles.subList(Math.min(highIndex1, lowIndex1), Math.max(highIndex2, lowIndex2) + 1), false));
                }
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectRisingWedge(List<CandleResDto> candles, int lookBack, int minDistance) {
        List<CandleResDto> maxima = findLocalMaxima(candles, lookBack);
        List<CandleResDto> minima = findLocalMinima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < Math.min(maxima.size(), minima.size()) - 1; i++) {
            CandleResDto high1 = maxima.get(i);
            CandleResDto high2 = maxima.get(i + 1);
            CandleResDto low1 = minima.get(i);
            CandleResDto low2 = minima.get(i + 1);

            int highIndex1 = candles.indexOf(high1);
            int highIndex2 = candles.indexOf(high2);
            int lowIndex1 = candles.indexOf(low1);
            int lowIndex2 = candles.indexOf(low2);

            if (Math.abs(highIndex1 - lowIndex1) > minDistance && Math.abs(highIndex2 - lowIndex2) > minDistance) {
                boolean isConverging = high2.getHigh().doubleValue() < high1.getHigh().doubleValue()
                        && low2.getLow().doubleValue() > low1.getLow().doubleValue();

                if (isConverging) {
                    patternsFound.add(new Pattern(PatternType.RISING_WEDGE, candles.subList(Math.min(highIndex1, lowIndex1), Math.max(highIndex2, lowIndex2) + 1), false));
                }
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectFallingWedge(List<CandleResDto> candles, int lookBack, int minDistance) {
        List<CandleResDto> maxima = findLocalMaxima(candles, lookBack);
        List<CandleResDto> minima = findLocalMinima(candles, lookBack);
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < Math.min(maxima.size(), minima.size()) - 1; i++) {
            CandleResDto high1 = maxima.get(i);
            CandleResDto high2 = maxima.get(i + 1);
            CandleResDto low1 = minima.get(i);
            CandleResDto low2 = minima.get(i + 1);

            int highIndex1 = candles.indexOf(high1);
            int highIndex2 = candles.indexOf(high2);
            int lowIndex1 = candles.indexOf(low1);
            int lowIndex2 = candles.indexOf(low2);

            if (Math.abs(highIndex1 - lowIndex1) > minDistance && Math.abs(highIndex2 - lowIndex2) > minDistance) {
                boolean isConverging = high2.getHigh().doubleValue() < high1.getHigh().doubleValue()
                        && low2.getLow().doubleValue() > low1.getLow().doubleValue();

                if (isConverging) {
                    patternsFound.add(new Pattern(PatternType.FALLING_WEDGE, candles.subList(Math.min(highIndex1, lowIndex1), Math.max(highIndex2, lowIndex2) + 1), false));
                }
            }
        }

        return patternsFound;
    }

    public List<Pattern> detectRectangle(List<CandleResDto> candles, int lookBack, double tolerancePercent) {
        List<Pattern> patternsFound = new ArrayList<>();

        for (int i = 0; i < candles.size() - lookBack; i++) {
            List<CandleResDto> subList = candles.subList(i, i + lookBack);

            double maxHigh = subList.stream().mapToDouble(c -> c.getHigh().doubleValue()).max().orElse(Double.MAX_VALUE);
            double minLow = subList.stream().mapToDouble(c -> c.getLow().doubleValue()).min().orElse(Double.MIN_VALUE);

            double range = maxHigh - minLow;
            boolean isRectangle = subList.stream().allMatch(c ->
                    (maxHigh - c.getHigh().doubleValue()) / range <= tolerancePercent &&
                            (c.getLow().doubleValue() - minLow) / range <= tolerancePercent
            );

            if (isRectangle) {
                patternsFound.add(new Pattern(PatternType.RECTANGLE, new ArrayList<>(subList), false));
            }
        }

        return patternsFound;
    }


    public boolean isBreakoutConfirmed(Pattern pattern, List<CandleResDto> candles) {
        if (pattern == null || candles == null || candles.isEmpty()) {
            throw new IllegalArgumentException("Pattern or candles cannot be null or empty.");
        }

        List<CandleResDto> patternCandleResDtos = pattern.getCandles();
        if (patternCandleResDtos == null || patternCandleResDtos.isEmpty()) {
            return false;
        }

        CandleResDto lastCandleResDto = candles.get(candles.size() - 1);
        double breakoutLevel;

        switch (pattern.getDirection()) {
            case BULLISH -> {
                breakoutLevel = patternCandleResDtos.stream()
                        .mapToDouble(c -> c.getHigh().doubleValue())
                        .max()
                        .orElse(Double.MIN_VALUE);
                return lastCandleResDto.getClose().doubleValue() > breakoutLevel;
            }
            case BEARISH -> {
                breakoutLevel = patternCandleResDtos.stream()
                        .mapToDouble(c -> c.getLow().doubleValue())
                        .min()
                        .orElse(Double.MAX_VALUE);
                return lastCandleResDto.getClose().doubleValue() < breakoutLevel;
            }
            case BOTH -> {
                double highLevel = patternCandleResDtos.stream()
                        .mapToDouble(c -> c.getHigh().doubleValue())
                        .max()
                        .orElse(Double.MIN_VALUE);

                double lowLevel = patternCandleResDtos.stream()
                        .mapToDouble(c -> c.getLow().doubleValue())
                        .min()
                        .orElse(Double.MAX_VALUE);

                return lastCandleResDto.getClose().doubleValue() > highLevel || lastCandleResDto.getClose().doubleValue() < lowLevel;
            }
            default -> throw new IllegalStateException("Unexpected pattern direction: " + pattern.getDirection());
        }
    }

    /**
     * Finds the local maxima in a list of candles using Streams.
     *
     * @param candles  List of OHLC candles
     * @param lookBack Number of candles before and after to consider
     * @return List of candles that are local maxima
     * @throws IllegalArgumentException if the input list is null, empty, or lookBack is invalid
     */
    public List<CandleResDto> findLocalMaxima(List<CandleResDto> candles, int lookBack) {
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
    public List<CandleResDto> findLocalMinima(List<CandleResDto> candles, int lookBack) {
        validateInput(candles, lookBack);
        return IntStream.range(lookBack, candles.size() - lookBack)
                .filter(i -> isLocalMinimum(candles, i, lookBack))
                .mapToObj(candles::get)
                .toList();
    }

    // --- Private Helper Methods ---

    private void validateInput(List<CandleResDto> candles, int lookBack) {
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

    private boolean isLocalMaximum(List<CandleResDto> candles, int index, int lookBack) {
        return IntStream.rangeClosed(index - lookBack, index + lookBack)
                .filter(j -> j != index)
                .allMatch(j -> candles.get(index).getHigh().doubleValue() > candles.get(j).getHigh().doubleValue());
    }

    private boolean isLocalMinimum(List<CandleResDto> candles, int index, int lookBack) {
        return IntStream.rangeClosed(index - lookBack, index + lookBack)
                .filter(j -> j != index)
                .allMatch(j -> candles.get(index).getLow().doubleValue() < candles.get(j).getLow().doubleValue());
    }

    private double calculateDifferencePercent(double value1, double value2) {
        return Math.abs(value1 - value2) / value1;
    }
}