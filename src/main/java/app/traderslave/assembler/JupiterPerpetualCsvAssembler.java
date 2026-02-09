package app.traderslave.assembler;

import app.traderslave.model.dto.req.JupiterPerpetualCsvReqDto;
import app.traderslave.model.dto.JupiterPerpetualCsvDto;
import app.traderslave.model.enums.Currency;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class JupiterPerpetualCsvAssembler {

    public JupiterPerpetualCsvDto toModel(List<JupiterPerpetualCsvReqDto> listTrades) {
        return JupiterPerpetualCsvDto.builder()
                .reports(buildReports(listTrades))
                .build();
    }

    private static Map<Currency, JupiterPerpetualCsvDto.Report> buildReports(List<JupiterPerpetualCsvReqDto> listTrades) {
        Map<Currency, JupiterPerpetualCsvDto.Report> reports = new EnumMap<>(Currency.class);

        listTrades.forEach(trade -> {
            var assetKey = trade.getAsset();
            var report = buildReport(trade, reports.get(assetKey));
            reports.put(assetKey, report);
        });

        return reports;
    }

    private static JupiterPerpetualCsvDto.Report buildReport(JupiterPerpetualCsvReqDto trade, JupiterPerpetualCsvDto.Report report) {
        if (report == null) {
            report = new JupiterPerpetualCsvDto.Report();
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
