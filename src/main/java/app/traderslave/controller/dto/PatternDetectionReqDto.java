package app.traderslave.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatternDetectionReqDto extends CandlesReqDto {
    /**
     * Number of candles to look back for local maxima/minima
     */
    private Integer lookBack = 5;

    /**
     * Tolerance for pattern similarity
     */
    private Double tolerancePercent = 0.02;

    /**
     * Minimum distance between patterns
     */
    private Integer minDistance = 3;

    /**
     * Whether to show all candles involved in the pattern
     */
    private Boolean showAllCandles = false;

    /**
     * Whether to return only patterns with confirmed breakouts
     */
    private Boolean onlyBreakoutConfirmed = true;
}
