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

        private Integer numberOfOpenTrades = 0;

        private Integer numberOfClosedTrades = 0;
        private Integer numberOfClosedTradersWithProfit = 0;
        private Integer numberOfClosedTradersWithLoss = 0;
        private Integer numberOfClosedTradersLiquidation = 0;

        private BigDecimal totalTradeSize = BigDecimal.ZERO;

        private BigDecimal totalTradeFees = BigDecimal.ZERO;
        private BigDecimal totalLiquidationFees = BigDecimal.ZERO;

        private BigDecimal totalProfitLoss = BigDecimal.ZERO;
        private BigDecimal totalDepositWithdraw = BigDecimal.ZERO;

        /* TODO add other field, for example
        private BigDecimal avgTradeSize = BigDecimal.ZERO
        private BigDecimal avgTradeSizeWithProfit = BigDecimal.ZERO;
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
                this.numberOfOpenTrades++;
            }
        }

        public void increaseNumberOfClosedTrades(JupiterPerpetualCsvReqDto trade) {
            if (trade.isDecrease()) {
                this.numberOfClosedTrades++;
            }
        }

        public void increaseNumberOfClosedTradesWithProfit(JupiterPerpetualCsvReqDto trade) {
            if (trade.isProfit()) {
                this.numberOfClosedTradersWithProfit++;
            }
        }

        public void increaseNumberOfClosedTradesWithLoss(JupiterPerpetualCsvReqDto trade) {
            if (!trade.isLiquidation() && trade.isLoss()) {
                this.numberOfClosedTradersWithLoss++;
            }
        }

        public void increaseNumberOfClosedTradesLiquidation(JupiterPerpetualCsvReqDto trade) {
            if (trade.isLiquidation()) {
                this.numberOfClosedTradersLiquidation++;
            }
        }

        public void increaseTotalTradeSize(JupiterPerpetualCsvReqDto trade) {
            if (trade.isDecrease()) {
                this.totalTradeSize = this.totalTradeSize.add(trade.getTradeSize());
            }
        }

        public void increaseTotalTradeFees(JupiterPerpetualCsvReqDto trade) {
            this.totalTradeFees = this.totalTradeFees.add(trade.getTradeFee());
        }

        public void increaseTotalLiquidationFees(JupiterPerpetualCsvReqDto trade) {
            this.totalLiquidationFees = this.totalLiquidationFees.add(trade.getLiquidationFee());
        }

        public void increaseTotalProfitLoss(JupiterPerpetualCsvReqDto trade) {
            this.totalProfitLoss = this.totalProfitLoss.add(trade.getProfitLoss());
        }

        public void increaseTotalDepositWithdraw(JupiterPerpetualCsvReqDto trade) {
            this.totalDepositWithdraw = this.totalDepositWithdraw.add(trade.getDepositWithdraw());
        }
    }
}
