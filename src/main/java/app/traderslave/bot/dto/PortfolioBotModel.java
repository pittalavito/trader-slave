package app.traderslave.bot.dto;


import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.OrderType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PortfolioBotModel {
        
        private Long id;
        private BigDecimal allTimeHigh;
        private BigDecimal capital;
        private BigDecimal allocatedCapital;
        private List<Order> openOrders;

        @Data
        @Builder
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