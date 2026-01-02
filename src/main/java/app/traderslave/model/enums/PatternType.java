package app.traderslave.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PatternType {

    DOUBLE_TOP("Double Top", PatternCategory.REVERSAL, Direction.BEARISH, "Inversione da rialzo a ribasso "),
    DOUBLE_BOTTOM("Double Bottom", PatternCategory.REVERSAL, Direction.BULLISH, "Inversione da ribasso a rialzo"),
    HEAD_SHOULDERS("Head & Shoulders", PatternCategory.REVERSAL, Direction.BEARISH, "Inversione da rialzo a ribasso"),
    INVERSE_HEAD_SHOULDERS("Inverse Head & Shoulders", PatternCategory.REVERSAL, Direction.BULLISH, "Inversione da ribasso a rialzo "),
    TRIPLE_TOP("Triple Top", PatternCategory.REVERSAL, Direction.BEARISH, "Inversione da rialzo a ribasso"),
    TRIPLE_BOTTOM("Triple Bottom", PatternCategory.REVERSAL, Direction.BULLISH, "Inversione da ribasso a rialzo "),
    ROUNDING_TOP("Rounding Top", PatternCategory.REVERSAL, Direction.BEARISH, "Inversione graduale da rialzo a ribasso"),
    ROUNDING_BOTTOM("Rounding Bottom", PatternCategory.REVERSAL, Direction.BULLISH, "Inversione graduale da ribasso a rialzo"),

    ASCENDING_TRIANGLE("Ascending Triangle", PatternCategory.CONTINUATION, Direction.BULLISH, "Probabile continuazione rialzista"),
    DESCENDING_TRIANGLE("Descending Triangle", PatternCategory.CONTINUATION, Direction.BEARISH, "Probabile continuazione ribassista"),
    SYMMETRICAL_TRIANGLE("Symmetrical Triangle", PatternCategory.CONTINUATION, Direction.BOTH, "Breakout possibile in entrambe le direzioni"),
    FLAG("Flag", PatternCategory.CONTINUATION, Direction.BOTH, "Continuazione del trend precedente"),
    PENNANT("Pennant", PatternCategory.CONTINUATION, Direction.BOTH, "Continuazione del trend precedente"),
    RISING_WEDGE("Rising Wedge", PatternCategory.REVERSAL, Direction.BEARISH, "Potenziale inversione ribassista"),
    FALLING_WEDGE("Falling Wedge", PatternCategory.REVERSAL, Direction.BULLISH, "Potenziale inversione rialzista"),
    RECTANGLE("Rectangle / Range", PatternCategory.CONTINUATION, Direction.BOTH, "Breakout possibile da range laterale");

    private final String displayName;
    private final PatternCategory category;
    private final Direction direction;
    private final String description;

    @Override
    public String toString() {
        return displayName + " (" + category + ", " + direction + ")";
    }

    public enum PatternCategory {
        REVERSAL,
        CONTINUATION
    }

    public enum Direction {
        BULLISH,
        BEARISH,
        BOTH
    }
}
