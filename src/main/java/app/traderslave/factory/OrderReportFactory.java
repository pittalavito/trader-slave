package app.traderslave.factory;

import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.dto.OrderReportDto;
import app.traderslave.domain.model.SimulationOrder;
import app.traderslave.model.enums.OrderType;
import app.traderslave.utils.ReportUtils;
import app.traderslave.utils.TimeUtils;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class OrderReportFactory {

    public OrderReportDto create(SimulationOrder order, List<CandleDto> candles) {
        boolean isLiquidated = false;
        CandleDto lastUtilCandle = new CandleDto();
        BigDecimal maxPriceDuringTrade = order.getOpenPrice();
        BigDecimal minPriceDuringTrade = order.getOpenPrice();

        for (CandleDto candle : candles) {
            lastUtilCandle = candle;
            maxPriceDuringTrade = maxPriceDuringTrade.max(candle.getHigh());
            minPriceDuringTrade = minPriceDuringTrade.min(candle.getLow());
            if (ReportUtils.isLiquidated(order, candle)) {
                isLiquidated = true;
                break;
            }
        }

        BigDecimal profitLoss = ReportUtils.calculateProfitLoss(order, lastUtilCandle.getClose());

        return OrderReportDto.builder()
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

    public OrderReportDto create(SimulationOrder order, List<CandleDto> candles, OrderReportDto rep1) {
        OrderReportDto lastReport = create(order, candles);

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

    public OrderReportDto create(SimulationOrder order) {
        return OrderReportDto.builder()
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
