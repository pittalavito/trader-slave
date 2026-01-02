package app.traderslave.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@Getter
@AllArgsConstructor
public enum PatternType {

    DOUBLE_TOP(Category.REVERSAL, Direction.BEARISH, Description.REVERSAL_BEARISH),
    DOUBLE_BOTTOM(Category.REVERSAL, Direction.BULLISH, Description.REVERSAL_BULLISH),
    HEAD_SHOULDERS(Category.REVERSAL, Direction.BEARISH, Description.REVERSAL_BEARISH),
    INVERSE_HEAD_SHOULDERS(Category.REVERSAL, Direction.BULLISH, Description.REVERSAL_BULLISH),
    TRIPLE_TOP(Category.REVERSAL, Direction.BEARISH, Description.REVERSAL_BEARISH),
    TRIPLE_BOTTOM(Category.REVERSAL, Direction.BULLISH, Description.REVERSAL_BULLISH),
    ROUNDING_TOP(Category.REVERSAL, Direction.BEARISH, Description.REVERSAL_GRADUAL_BEARISH),
    ROUNDING_BOTTOM(Category.REVERSAL, Direction.BULLISH, Description.REVERSAL_GRADUAL_BULLISH),
    ASCENDING_TRIANGLE(Category.CONTINUATION, Direction.BULLISH, Description.CONTINUATION_BULLISH),
    DESCENDING_TRIANGLE(Category.CONTINUATION, Direction.BEARISH, Description.CONTINUATION_BEARISH),
    SYMMETRICAL_TRIANGLE(Category.CONTINUATION, Direction.BOTH, Description.CONTINUATION_BOTH),
    FLAG(Category.CONTINUATION, Direction.BOTH, Description.CONTINUATION_NEUTRAL),
    PENNANT(Category.CONTINUATION, Direction.BOTH, Description.CONTINUATION_NEUTRAL),
    RISING_WEDGE(Category.REVERSAL, Direction.BEARISH, Description.POTENTIAL_BEARISH_REVERSAL),
    FALLING_WEDGE(Category.REVERSAL, Direction.BULLISH, Description.POTENTIAL_BULLISH_REVERSAL),
    RECTANGLE(Category.CONTINUATION, Direction.BOTH, Description.POTENTIAL_LATERAL_BREAKOUT);

    private final Category category;
    private final Direction direction;
    private final String description;

    @Override
    public String toString() {
        return name() + " (" + category + ", " + direction + ")";
    }

    public enum Category {
        REVERSAL,
        CONTINUATION
    }

    public enum Direction {
        BULLISH,
        BEARISH,
        BOTH
    }

    @UtilityClass
    public class Description {
        public static final String REVERSAL_BEARISH = "Inversione da rialzo a ribasso";
        public static final String REVERSAL_BULLISH = "Inversione da ribasso a rialzo";
        public static final String REVERSAL_GRADUAL_BEARISH = "Inversione graduale da rialzo a ribasso";
        public static final String REVERSAL_GRADUAL_BULLISH = "Inversione graduale da ribasso a rialzo";

        public static final String CONTINUATION_BULLISH = "Probabile continuazione rialzista";
        public static final String CONTINUATION_BEARISH = "Probabile continuazione ribassista";
        public static final String CONTINUATION_BOTH = "Breakout possibile in entrambe le direzioni";
        public static final String CONTINUATION_NEUTRAL = "Continuazione del trend precedente";

        public static final String POTENTIAL_BULLISH_REVERSAL = "Potenziale inversione rialzista";
        public static final String POTENTIAL_BEARISH_REVERSAL = "Potenziale inversione ribassista";
        public static final String POTENTIAL_LATERAL_BREAKOUT = "Potenziale breakout da range laterale";
    }
}
