package app.traderslave.factory;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.model.domain.ReportOrder;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.OrderType;
import app.traderslave.utility.ReportOrderUtils;
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
            minPriceDuringTrade = maxPriceDuringTrade.min(candle.getLow());
            if (ReportOrderUtils.isLiquidated(order, candle)) {
                isLiquidated = true;
                break;
            }
        }

        BigDecimal profitLoss = ReportOrderUtils.calculateProfitLoss(order, lastUtilCandle.getClose());

        return ReportOrder.builder()
                .liquidated(isLiquidated)
                .closeTime(lastUtilCandle.getCloseTime().toString())
                .profitLoss(profitLoss)
                .profitLossMinusFees(profitLoss)
                .closePrice(lastUtilCandle.getClose())
                .maxUnrealizedLossDuringTrade(ReportOrderUtils.calculateProfitLoss(order, OrderType.BUY == order.getType() ? minPriceDuringTrade : maxPriceDuringTrade))
                .maxUnrealizedProfitDuringTrade(ReportOrderUtils.calculateProfitLoss(order, OrderType.BUY == order.getType() ? maxPriceDuringTrade : minPriceDuringTrade))
                .durationOfTradeInSeconds(TimeUtils.calculateDiffInSecond(order.getOpenTime(), lastUtilCandle.getCloseTime()))
                .percentageChange(ReportOrderUtils.calculateProfitLossPercentage(order, profitLoss))
                .build();
    }


}
