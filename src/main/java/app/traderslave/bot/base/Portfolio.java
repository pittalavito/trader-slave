package app.traderslave.bot.base;

import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.OrderType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public abstract class Portfolio <R> {

    public abstract Model get(R request);

    @Data
    public static class Model {
        
        private BigDecimal allTimeHigh;
        private BigDecimal capital;
        private BigDecimal allocatedCapital;
        private List<Order> order;

        @Data
        public static class Order {
            private CurrencyPair currencyPair;
            private OrderType orderType;
            private BigDecimal allocatedCapital;
            private BigDecimal quantity;
            private BigDecimal entryPrice;
            private BigDecimal currentPrice;
            private BigDecimal profitLossInPercent;
            private BigDecimal durationInMinutes;
            private LocalDateTime openTime;
            private Object openSignal;
        }
    }
}
