package app.traderslave.utility;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.OrderType;
import app.traderslave.model.report.OrderReport;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import java.math.BigDecimal;

@UtilityClass
public class ReportUtils {

    public BigDecimal calculateLiquidationPrice(BigDecimal currentPrice, OrderType type, int leverage) {
        BigDecimal percentage;
        if (OrderType.BUY.equals(type)) {
            percentage = BigDecimal.valueOf(1 - 0.95/leverage);
        } else {
            percentage = BigDecimal.valueOf(1 + 0.95/leverage);
        }
        return currentPrice.multiply(percentage);
    }

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
        return profitLoss
                .divide(order.getAmountOfTrade(), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public @Nullable Boolean isProfit(OrderReport report) {
        Assert.notNull(report.getProfitLoss(), "report get profit loss cannot be null");
        Boolean result = null;
        if (report.getProfitLoss().doubleValue() > 0) {
            result = Boolean.TRUE;
        } else if (report.getProfitLoss().doubleValue() < 0) {
            result = Boolean.FALSE;
        }
        return result;
    }
}
