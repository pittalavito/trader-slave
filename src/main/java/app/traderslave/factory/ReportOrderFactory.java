package app.traderslave.factory;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.model.report.ReportOrder;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.OrderType;
import app.traderslave.utility.ReportUtils;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.util.Comparator;

@UtilityClass
public class ReportOrderFactory {

    public ReportOrder create(SimulationOrder order, CandlesResDto candles) {
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

        return buildReportOrder(order, lastUtilCandle, isLiquidated, profitLoss, maxPriceDuringTrade, minPriceDuringTrade);
    }

    public ReportOrder create(SimulationOrder order, CandlesResDto candles, ReportOrder rep1) {
        ReportOrder lastReport = create(order, candles);
        return create(order, rep1, lastReport);
    }


    private ReportOrder create(SimulationOrder order, ReportOrder rep1, ReportOrder lastReport) {
        BigDecimal maxUnrealizedProfitDuringTrade = rep1.getMaxUnrealizedProfitDuringTrade().max(lastReport.getMaxUnrealizedProfitDuringTrade());
        BigDecimal maxUnrealizedLossDuringTrade = rep1.getMaxUnrealizedLossDuringTrade().min(lastReport.getMaxUnrealizedLossDuringTrade());
        return ReportOrder.builder()
                .liquidated(lastReport.isLiquidated())
                .closePrice(lastReport.getClosePrice())
                .closeTime(lastReport.getCloseTime())
                .profitLoss(lastReport.getProfitLoss())
                .profitLossMinusFees(lastReport.getProfitLossMinusFees())
                .maxUnrealizedLossDuringTrade(maxUnrealizedLossDuringTrade)
                .maxUnrealizedProfitDuringTrade(maxUnrealizedProfitDuringTrade)
                .durationOfTradeInSeconds(TimeUtils.calculateDiffInSecond(order.getOpenTime(), lastReport.getCloseTime()))
                .percentageChange(lastReport.getPercentageChange())
                .build();
    }


    /**
     * For order with status not open
     */
    public ReportOrder create(SimulationOrder order) {
        return ReportOrder.builder()
                .closePrice(order.getClosePrice())
                .closeTime(order.getCloseTime())
                .profitLoss(order.getProfitLoss())
                .profitLossMinusFees(order.getProfitLossMinusFees())
                .maxUnrealizedLossDuringTrade(order.getMaxUnrealizedLossDuringTrade())
                .maxUnrealizedProfitDuringTrade(order.getMaxUnrealizedProfitDuringTrade())
                .durationOfTradeInSeconds(order.getDurationOfTradeInSeconds())
                .percentageChange(order.getPercentageChange())
                .build();
    }

    private ReportOrder buildReportOrder(SimulationOrder order, CandleResDto lastUtilCandle, boolean isLiquidated, BigDecimal profitLoss, BigDecimal maxPriceDuringTrade, BigDecimal minPriceDuringTrade) {
        return ReportOrder.builder()
                .liquidated(isLiquidated)
                .closeTime(lastUtilCandle.getCloseTime())
                .profitLoss(profitLoss)
                .profitLossMinusFees(profitLoss)
                .closePrice(lastUtilCandle.getClose())
                .maxUnrealizedLossDuringTrade(ReportUtils.calculateProfitLoss(order, OrderType.BUY == order.getType() ? minPriceDuringTrade : maxPriceDuringTrade))
                .maxUnrealizedProfitDuringTrade(ReportUtils.calculateProfitLoss(order, OrderType.BUY == order.getType() ? maxPriceDuringTrade : minPriceDuringTrade))
                .durationOfTradeInSeconds(TimeUtils.calculateDiffInSecond(order.getOpenTime(), lastUtilCandle.getCloseTime()))
                .percentageChange(ReportUtils.calculateProfitLossPercentage(order, profitLoss))
                .build();
    }
}
