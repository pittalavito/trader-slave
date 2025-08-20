package app.traderslave.assembler;

import app.traderslave.controller.dto.JupiterPerpetualCsvReqDto;
import app.traderslave.controller.dto.JupiterPerpetualCsvResDto;
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

        report.increaseTotalTradeSize(trade);

        report.increaseTotalTradeFees(trade);
        report.increaseTotalLiquidationFees(trade);

        report.increaseTotalProfitLoss(trade);
        report.increaseTotalDepositWithdraw(trade);

        return report;
    }
}
