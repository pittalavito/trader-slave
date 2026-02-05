package app.traderslave.assembler;

import app.traderslave.model.dto.req.JupiterPerpetualCsvReqDto;
import app.traderslave.model.dto.res.JupiterPerpetualCsvResDto;
import app.traderslave.model.enums.Currency;
import lombok.experimental.UtilityClass;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class JupiterPerpetualCsvAssembler {

    public JupiterPerpetualCsvResDto toModel(List<JupiterPerpetualCsvReqDto> listTrades) {
        return JupiterPerpetualCsvResDto.builder()
                .reports(buildReports(listTrades))
                .build();
    }

    private static Map<Currency, JupiterPerpetualCsvResDto.Report> buildReports(List<JupiterPerpetualCsvReqDto> listTrades) {
        Map<Currency, JupiterPerpetualCsvResDto.Report> reports = new EnumMap<>(Currency.class);

        listTrades.forEach(trade -> {
            var assetKey = trade.getAsset();
            var report = buildReport(trade, reports.get(assetKey));
            reports.put(assetKey, report);
        });

        return reports;
    }

    private static JupiterPerpetualCsvResDto.Report buildReport(JupiterPerpetualCsvReqDto trade, JupiterPerpetualCsvResDto.Report report) {
        if (report == null) {
            report = new JupiterPerpetualCsvResDto.Report();
        }

        report.increaseNumberOfOpenTrades(trade);

        report.increaseNumberOfClosedTrades(trade);
        report.increaseNumberOfClosedTradesWithProfit(trade);
        report.increaseNumberOfClosedTradesWithLoss(trade);
        report.increaseNumberOfClosedTradesLiquidation(trade);

        report.increaseTotalSize(trade);
        report.increaseTotalProfitSize(trade);
        report.increaseTotalLossSize(trade);
        report.increaseTotalLiquidationSize(trade);

        report.increaseTotalTradeFees(trade);
        report.increaseTotalLiquidationFees(trade);

        report.increaseTotalProfitLoss(trade);
        report.increaseTotalDepositWithdraw(trade);

        report.increaseAvgTradeProfit(trade);
        report.increaseAvgTradeLoss(trade);
        report.increaseAvgTradeLiquidation(trade);

        return report;
    }
}
