package app.traderslave.utility;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.OrderType;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;

@UtilityClass
public class ReportOrderUtils {

    public boolean isLiquidated(SimulationOrder order, CandleResDto candle) {
        if (OrderType.BUY == order.getType()) {
            return order.getLiquidationPrice().compareTo(candle.getLow()) >= 0;
        } else {
            return order.getLiquidationPrice().compareTo(candle.getHigh()) <= 0;
        }
    }

    public BigDecimal calculateProfitLoss(SimulationOrder order, BigDecimal closePrice) {
        BigDecimal initialAmountSize = BigDecimal.valueOf(order.getLeverage() * order.getAmountOfTrade().doubleValue());
        BigDecimal quantity = initialAmountSize.divide(order.getOpenPrice(), 8, java.math.RoundingMode.HALF_UP);
        BigDecimal actualAmountSize = quantity.multiply(closePrice);

        if (OrderType.BUY == order.getType()) {
            return actualAmountSize.subtract(initialAmountSize);
        } else {
            return initialAmountSize.subtract(actualAmountSize);
        }
    }

    public BigDecimal calculateProfitLossPercentage(SimulationOrder order, BigDecimal profitLoss) {
        return profitLoss.divide(order.getAmountOfTrade(), 2, java.math.RoundingMode.HALF_UP);
    }
}
