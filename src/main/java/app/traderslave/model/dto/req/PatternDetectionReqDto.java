package app.traderslave.model.dto.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatternDetectionReqDto extends CandlesReqDto {

    /** Number of candles to look back for local maxima/minima */
    private Integer lookBack = 5;

    /** Tolerance percentage for pattern recognition */
    private Double tolerancePercent = 0.01;

    /** Minimum distance between key points in the pattern */
    private Integer minDistance = 3;

    /** Whether to include all candles forming the pattern in the response */
    private Boolean showAllCandles = false;

    /** Whether to filter only patterns with confirmed breakout */
    private Boolean onlyBreakoutConfirmed = true;
}
