package app.traderslave.assembler.backtest;

import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.model.dto.BackTestOrderDto;
import app.traderslave.model.dto.req.TimeReqDto;
import app.traderslave.utils.ReportUtils;
import org.springframework.stereotype.Component;

@Component
public class BackTestCloseOrderAssembler {

    public BackTestOrderDto toModel(BackTestOrder order, TimeReqDto dto) {
        return BackTestOrderDto.builder()
                .requestInfo(buildRequestInfo(dto))
                .orderType(order.getType())
                .amountOfTrade(order.getAmountOfTrade())
                .simulationId(order.getPortfolioId())
                .orderId(order.getId())
                .status(mappingStatus(order.getStatus(), ReportUtils.isProfit(order)))
                .openPrice(order.getOpenPrice())
                .closePrice(order.getClosePrice())
                .openTime(order.getOpenTime())
                .closeTime(order.getCloseTime())
                .leverage(order.getLeverage())
                .liquidationPrice(order.getLiquidationPrice())
                .profitLoss(order.getProfitLoss())
                .percentageChange(order.getPercentageChange())
                .maxUnrealizedLossDuringTrade(order.getMaxUnrealizedLossDuringTrade())
                .maxUnrealizedProfitDuringTrade(order.getMaxUnrealizedProfitDuringTrade())
                .durationOfTradeInSeconds(order.getDurationOfTradeInSeconds())
                .percentageChange(order.getPercentageChange())
                .maxPriceDuringTrade(order.getMaxPriceDuringTrade())
                .minPriceDuringTrade(order.getMinPriceDuringTrade())
                .build();
    }

    private String buildRequestInfo(TimeReqDto dto) {
        return dto.isRealTimeRequest() ? "REAL-TIME-REQUEST" : "BACK-TIME-REQUEST " + dto.getStartTime();
    }

    private BackTestOrderDto.Status mappingStatus(BackTestOrder.Status orderStatus, Boolean isProfit) {
        if (orderStatus == BackTestOrder.Status.LIQUIDATED) {
            return BackTestOrderDto.Status.LIQUIDATED;
        }
        return switch (orderStatus) {
            case OPEN ->
                    mappingStatus(isProfit, BackTestOrderDto.Status.OPEN_WITH_PROFIT, BackTestOrderDto.Status.OPEN_WITH_LOSS, BackTestOrderDto.Status.OPEN_NEUTRAL);
            case CLOSED ->
                    mappingStatus(isProfit, BackTestOrderDto.Status.CLOSED_WITH_PROFIT, BackTestOrderDto.Status.CLOSED_WITH_LOSS, BackTestOrderDto.Status.CLOSED_NEUTRAL);
            default ->
                    throw new CustomException(ExceptionEnum.STATUS_NOT_ALLOWED_FOR_THIS_METHOD);
        };
    }

    private BackTestOrderDto.Status mappingStatus(Boolean isProfit, BackTestOrderDto.Status profitStatus, BackTestOrderDto.Status lossStatus, BackTestOrderDto.Status neutralStatus) {
        if (Boolean.TRUE.equals(isProfit)) {
            return profitStatus;
        } else if (Boolean.FALSE.equals(isProfit)) {
            return lossStatus;
        } else {
            return neutralStatus;
        }
    }
}
