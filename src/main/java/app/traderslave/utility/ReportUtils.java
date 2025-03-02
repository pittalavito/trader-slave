package app.traderslave.utility;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.OrderType;
import app.traderslave.model.report.AiReportOrder;
import app.traderslave.model.report.ReportOrder;
import lombok.experimental.UtilityClass;
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

    public AiReportOrder.StatusOrder calculateAiStatusOrder(SimulationOrder order, ReportOrder reportOrder) {
        AiReportOrder.StatusOrder result;
        boolean isProfit = reportOrder.getProfitLoss().doubleValue() > 0;

        switch (order.getStatus()) {
            case LIQUIDATED -> result = AiReportOrder.StatusOrder.LIQUIDATED;
            case OPEN -> result = isProfit ? AiReportOrder.StatusOrder.OPEN_WITH_PROFIT : AiReportOrder.StatusOrder.OPEN_WITH_LOSS;
            case CLOSED ->  result = isProfit ? AiReportOrder.StatusOrder.CLOSED_WITH_PROFIT : AiReportOrder.StatusOrder.CLOSED_WITH_LOSS;
            default -> throw new CustomException(ExceptionEnum.AI_REPORT_ORDER_ERROR);
        }

        return result;
    }
}
