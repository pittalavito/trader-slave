package app.traderslave.domain.factory;

import app.traderslave.model.dto.req.BackTestCreateOrderReqDto;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.dto.OrderReportDto;
import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.utils.ReportUtils;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class BackTestOrderFactory {

    public BackTestOrder create(BackTestPortfolio backTestPortfolio, BackTestCreateOrderReqDto dto, CandleDto candle) {
        return BackTestOrder.builder()
                .portfolioId(dto.getSimulationId())
                .amountOfTrade(dto.getAmountOfTrade() == null ? backTestPortfolio.getBalance() : dto.getAmountOfTrade())
                .openPrice(candle.getClose())
                .openTime(candle.getCloseTime())
                .status(BackTestOrder.Status.OPEN)
                .type(dto.getOrderType())
                .uid(UUID.randomUUID().toString())
                .version(0)
                .liquidationPrice(ReportUtils.calculateLiquidationPrice(candle.getClose(), dto.getOrderType(), dto.getLeverage()))
                .leverage(dto.getLeverage())
                .build();
    }

    public BackTestOrder close(BackTestOrder order, OrderReportDto report, boolean endSimulation) {
        if(!endSimulation) {
            order.setStatus(report.isLiquidated() ? BackTestOrder.Status.LIQUIDATED : BackTestOrder.Status.CLOSED);
        }
        order.setClosePrice(report.getClosePrice());
        order.setCloseTime(report.getCloseTime());
        order.setProfitLoss(report.getProfitLoss());
        order.setPercentageChange(report.getPercentageChange());
        order.setMaxUnrealizedProfitDuringTrade(report.getMaxUnrealizedProfitDuringTrade());
        order.setMaxUnrealizedLossDuringTrade(report.getMaxUnrealizedLossDuringTrade());
        order.setDurationOfTradeInSeconds(report.getDurationOfTradeInSeconds());
        order.setLastModificationDate(LocalDateTime.now());
        order.setVersion(order.getVersion() + 1);
        order.setMaxPriceDuringTrade(report.getMaxPriceDuringTrade());
        order.setMinPriceDuringTrade(report.getMinPriceDuringTrade());
        return order;
    }
}
