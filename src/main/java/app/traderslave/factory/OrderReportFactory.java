package app.traderslave.factory;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.model.report.OrderReport;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.OrderType;
import app.traderslave.utility.ReportUtils;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.util.Comparator;

@UtilityClass
public class OrderReportFactory {

    public OrderReport create(SimulationOrder order, CandlesResDto candles) {
        boolean isLiquidated = false;
        CandleResDto lastUtilCandle = new CandleResDto();
        BigDecimal maxPriceDuringTrade = order.getOpenPrice();
        BigDecimal minPriceDuringTrade = order.getOpenPrice();

        candles.getList().sort(Comparator.comparing(CandleResDto::getCloseTime));
        for (CandleResDto candle : candles.getList()) {
            lastUtilCandle = candle;
            maxPriceDuringTrade = maxPriceDuringTrade.max(candle.getHigh());
            minPriceDuringTrade = minPriceDuringTrade.min(candle.getLow());
            if (ReportUtils.isLiquidated(order, candle)) {
                isLiquidated = true;
                break;
            }
        }

        BigDecimal profitLoss = ReportUtils.calculateProfitLoss(order, lastUtilCandle.getClose());

        return OrderReport.builder()
                .liquidated(isLiquidated)
                .closeTime(lastUtilCandle.getCloseTime())
                .profitLoss(profitLoss)
                .profitLossMinusFees(profitLoss)
                .closePrice(isLiquidated ? lastUtilCandle.getHigh() : lastUtilCandle.getClose())
                .maxUnrealizedLossDuringTrade(ReportUtils.calculateProfitLoss(order, OrderType.BUY == order.getType() ? minPriceDuringTrade : maxPriceDuringTrade))
                .maxUnrealizedProfitDuringTrade(ReportUtils.calculateProfitLoss(order, OrderType.BUY == order.getType() ? maxPriceDuringTrade : minPriceDuringTrade))
                .durationOfTradeInSeconds(TimeUtils.calculateDiffInSecond(order.getOpenTime(), lastUtilCandle.getCloseTime()))
                .percentageChange(ReportUtils.calculateProfitLossPercentage(order, profitLoss))
                .maxPriceDuringTrade(maxPriceDuringTrade)
                .minPriceDuringTrade(minPriceDuringTrade)
                .build();
    }

    public OrderReport create(SimulationOrder order, CandlesResDto candles, OrderReport rep1) {
        OrderReport lastReport = create(order, candles);

        BigDecimal maxUnrealizedProfitDuringTrade = rep1.getMaxUnrealizedProfitDuringTrade().max(lastReport.getMaxUnrealizedProfitDuringTrade());
        BigDecimal maxUnrealizedLossDuringTrade = rep1.getMaxUnrealizedLossDuringTrade().min(lastReport.getMaxUnrealizedLossDuringTrade());
        BigDecimal maxPriceDuringTrade = rep1.getMaxPriceDuringTrade().max(lastReport.getMaxPriceDuringTrade());
        BigDecimal minPriceDuringTrade = rep1.getMinPriceDuringTrade().min(lastReport.getMinPriceDuringTrade());

        lastReport.setMaxPriceDuringTrade(maxPriceDuringTrade);
        lastReport.setMinPriceDuringTrade(minPriceDuringTrade);
        lastReport.setMaxUnrealizedProfitDuringTrade(maxUnrealizedProfitDuringTrade);
        lastReport.setMaxUnrealizedLossDuringTrade(maxUnrealizedLossDuringTrade);
        return lastReport;
    }

    public OrderReport create(SimulationOrder order) {
        return OrderReport.builder()
                .closePrice(order.getClosePrice())
                .closeTime(order.getCloseTime())
                .profitLoss(order.getProfitLoss())
                .maxUnrealizedLossDuringTrade(order.getMaxUnrealizedLossDuringTrade())
                .maxUnrealizedProfitDuringTrade(order.getMaxUnrealizedProfitDuringTrade())
                .durationOfTradeInSeconds(order.getDurationOfTradeInSeconds())
                .percentageChange(order.getPercentageChange())
                .minPriceDuringTrade(order.getMinPriceDuringTrade())
                .maxPriceDuringTrade(order.getMaxPriceDuringTrade())
                .build();
    }
}
