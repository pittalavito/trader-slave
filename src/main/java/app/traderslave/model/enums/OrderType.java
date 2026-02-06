package app.traderslave.model.enums;

public enum OrderType {
    BUY,
    SELL;

    public static OrderType map(Signal signal) {
        return switch (signal) {
            case BUY -> OrderType.BUY;
            case SELL -> OrderType.SELL;
            default -> null;
        };
    }
}
