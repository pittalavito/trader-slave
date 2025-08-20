package app.traderslave.controller.dto;

import app.traderslave.model.enums.Currency;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class JupiterPerpetualCsvResDto {

    private Map<Currency, Report> reports;

    @Data
    public static class Report {

        private Integer numOpen = 0;

        private Integer numClosed = 0;
        private Integer numClosedProfit = 0;
        private Integer numClosedLoss = 0;
        private Integer numClosedLiquidation = 0;

        private BigDecimal totalSize = BigDecimal.ZERO;
        private BigDecimal totalProfitSize = BigDecimal.ZERO;
        private BigDecimal totalLossSize = BigDecimal.ZERO;
        private BigDecimal totalLiquidationSize = BigDecimal.ZERO;

        private BigDecimal totalTradeFees = BigDecimal.ZERO;
        private BigDecimal totalLiquidationFees = BigDecimal.ZERO;

        private BigDecimal totalProfitLoss = BigDecimal.ZERO;
        private BigDecimal totalDepositWithdraw = BigDecimal.ZERO;

        private BigDecimal avgTradeProfit = BigDecimal.ZERO;
        private BigDecimal avgTradeLoss = BigDecimal.ZERO;
        private BigDecimal avgTradeLiquidation = BigDecimal.ZERO;

        /* TODO add other field, for example
        private BigDecimal avgTradeSize = BigDecimal.ZERO
        private BigDecimal avgTradeSizeWithLoss = BigDecimal.ZERO;
        private BigDecimal avgTradeSizeLiquidation = BigDecimal.ZERO;
        private BigDecimal avgTradeForDay = BigDecimal.ZERO
        private BigDecimal avgTradeFee = BigDecimal.ZERO;
        private BigDecimal avgTradesWithProfit = BigDecimal.ZERO;
        private BigDecimal avgTradesWithLoss = BigDecimal.ZERO;
        private BigDecimal avgTradesLiquidation = BigDecimal.ZERO;
        */


        public void increaseNumberOfOpenTrades(JupiterPerpetualCsvReqDto trade) {
            if (trade.isIncrease()) {
                numOpen++;
            }
        }

        public void increaseNumberOfClosedTrades(JupiterPerpetualCsvReqDto trade) {
            if (trade.isDecrease()) {
                numClosed++;
            }
        }

        public void increaseNumberOfClosedTradesWithProfit(JupiterPerpetualCsvReqDto trade) {
            if (trade.isProfit()) {
                numClosedProfit++;
            }
        }

        public void increaseNumberOfClosedTradesWithLoss(JupiterPerpetualCsvReqDto trade) {
            if (!trade.isLiquidation() && trade.isLoss()) {
                numClosedLoss++;
            }
        }

        public void increaseNumberOfClosedTradesLiquidation(JupiterPerpetualCsvReqDto trade) {
            if (trade.isLiquidation()) {
                numClosedLiquidation++;
            }
        }

        public void increaseTotalSize(JupiterPerpetualCsvReqDto trade) {
            if (trade.isDecrease()) {
                totalSize = totalSize.add(trade.getTradeSize());
            }
        }

        public void increaseTotalProfitSize(JupiterPerpetualCsvReqDto trade) {
            if (trade.isProfit()) {
                totalProfitSize = totalProfitSize.add(trade.getTradeSize());
            }
        }

        public void increaseTotalLossSize(JupiterPerpetualCsvReqDto trade) {
            if (!trade.isLiquidation() && trade.isLoss()) {
                totalLossSize = totalLossSize.add(trade.getTradeSize());
            }
        }

        public void increaseTotalLiquidationSize(JupiterPerpetualCsvReqDto trade) {
            if (trade.isLiquidation()) {
                totalLiquidationSize = totalLiquidationSize.add(trade.getTradeSize());
            }
        }

        public void increaseTotalTradeFees(JupiterPerpetualCsvReqDto trade) {
            totalTradeFees = totalTradeFees.add(trade.getTradeFee());
        }

        public void increaseTotalLiquidationFees(JupiterPerpetualCsvReqDto trade) {
            totalLiquidationFees = totalLiquidationFees.add(trade.getLiquidationFee());
        }

        public void increaseTotalProfitLoss(JupiterPerpetualCsvReqDto trade) {
            totalProfitLoss = totalProfitLoss.add(trade.getProfitLoss());
        }

        public void increaseTotalDepositWithdraw(JupiterPerpetualCsvReqDto trade) {
            totalDepositWithdraw = totalDepositWithdraw.add(trade.getDepositWithdraw());
        }

        public void increaseAvgTradeProfit(JupiterPerpetualCsvReqDto trade) {
            if (!trade.isProfit()) {
                return;
            }
            if (numClosedProfit > 1) {
                var normalized = avgTradeProfit.doubleValue() * (numClosedProfit - 1);
                BigDecimal addedSize = BigDecimal.valueOf(normalized).add(trade.getProfitLoss());
                avgTradeProfit = addedSize.divide(BigDecimal.valueOf(numClosedProfit), 2, java.math.RoundingMode.HALF_UP);
            } else {
                avgTradeProfit = trade.getProfitLoss();
            }
        }

        public void increaseAvgTradeLoss(JupiterPerpetualCsvReqDto trade) {
            if (trade.isLiquidation() || !trade.isLoss()) {
                return;
            }
            if (numClosedLoss > 1) {
                var normalized = avgTradeLoss.doubleValue() * (numClosedLoss - 1);
                BigDecimal addedSize = BigDecimal.valueOf(normalized).add(trade.getProfitLoss());
                avgTradeLoss = addedSize.divide(BigDecimal.valueOf(numClosedLoss), 2, java.math.RoundingMode.HALF_UP);
            } else {
                avgTradeLoss = trade.getProfitLoss();
            }
        }

        public void increaseAvgTradeLiquidation(JupiterPerpetualCsvReqDto trade) {
            if (!trade.isLiquidation()) {
                return;
            }
            if (numClosedLiquidation > 1) {
                var normalized = avgTradeLiquidation.doubleValue() * (numClosedLiquidation - 1);
                BigDecimal addedSize = BigDecimal.valueOf(normalized).add(trade.getProfitLoss());
                avgTradeLiquidation = addedSize.divide(BigDecimal.valueOf(numClosedLiquidation), 2, java.math.RoundingMode.HALF_UP);
            } else {
                avgTradeLiquidation = trade.getProfitLoss();
            }
        }
    }
}
