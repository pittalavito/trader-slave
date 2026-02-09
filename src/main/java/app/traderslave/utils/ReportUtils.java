package app.traderslave.utils;

import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.enums.OrderType;
import app.traderslave.model.dto.OrderReportDto;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import java.math.BigDecimal;

@UtilityClass
public class ReportUtils {

    public BigDecimal calculateLiquidationPrice(BigDecimal currentPrice, OrderType type, int leverage) {
        BigDecimal percentage;
        if (OrderType.BUY.equals(type)) {
            percentage = BigDecimal.valueOf(1 - 0.90/leverage);
        } else {
            percentage = BigDecimal.valueOf(1 + 0.90/leverage);
        }
        return currentPrice.multiply(percentage);
    }

    public boolean isLiquidated(BackTestOrder order, CandleDto candle) {
        if (OrderType.BUY == order.getType()) {
            return order.getLiquidationPrice().compareTo(candle.getLow()) >= 0;
        } else {
            return order.getLiquidationPrice().compareTo(candle.getHigh()) <= 0;
        }
    }

    public BigDecimal calculateProfitLoss(BackTestOrder order, BigDecimal closePrice) {
        BigDecimal initialAmountSize = BigDecimal.valueOf(order.getLeverage() * order.getAmountOfTrade().doubleValue());
        BigDecimal quantity = initialAmountSize.divide(order.getOpenPrice(), 8, java.math.RoundingMode.HALF_UP);
        BigDecimal actualAmountSize = quantity.multiply(closePrice);
        if (OrderType.BUY == order.getType()) {
            return actualAmountSize.subtract(initialAmountSize);
        } else {
            return initialAmountSize.subtract(actualAmountSize);
        }
    }

    public BigDecimal calculateProfitLossPercentage(BackTestOrder order, BigDecimal profitLoss) {
        return profitLoss
                .divide(order.getAmountOfTrade(), 2, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal calculatePercentageNumOrder(Integer numOrder, Integer numOrderType) {
        if (numOrderType == 0 || numOrder == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numOrderType)
                .divide(BigDecimal.valueOf(numOrder), 2, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal calculatePercentage(BigDecimal startBalance, BigDecimal endBalance) {
        BigDecimal percentage = endBalance
            .divide(startBalance, 2, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));

    if (endBalance.compareTo(startBalance) > 0) {
        return percentage.subtract(BigDecimal.valueOf(100));
    } else {
        return BigDecimal.valueOf(100).subtract(percentage).negate();
    }
}

    public @Nullable Boolean isProfit(BackTestOrder order) {
        Assert.notNull(order.getProfitLoss(), "report get profit loss cannot be null");
        return isProfit(order.getProfitLoss());
    }

    public @Nullable Boolean isProfit(OrderReportDto report) {
        Assert.notNull(report.getProfitLoss(), "report get profit loss cannot be null");
        return isProfit(report.getProfitLoss());
    }

    public @Nullable Boolean isProfit(@NotNull BigDecimal profitLoss) {
        Boolean result = null;
        if (profitLoss.doubleValue() > 0) {
            result = Boolean.TRUE;
        } else if (profitLoss.doubleValue() < 0) {
            result = Boolean.FALSE;
        }
        return result;
    }
}
