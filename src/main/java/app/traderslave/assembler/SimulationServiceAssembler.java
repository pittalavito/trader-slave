package app.traderslave.assembler;

import app.traderslave.controller.dto.*;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.report.OrderReport;
import app.traderslave.utility.ReportUtils;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import java.util.List;
import java.util.Map;

@UtilityClass
public class SimulationServiceAssembler {

    public PostSimulationResDto toModelCreate(Simulation simulation) {
        return PostSimulationResDto.builder()
                .simulationId(simulation.getId())
                .currencyPair(simulation.getCurrencyPair())
                .description(simulation.getDescription())
                .balance(simulation.getBalance())
                .currency(simulation.getCurrency())
                .build();
    }

    public SimulationOrderResDto toModelCreateOrder(SimulationOrder order, TimeReqDto dto) {
        return SimulationOrderResDto.builder()
                .requestInfo(buildRequestInfo(dto))
                .orderId(order.getId())
                .simulationId(order.getSimulationId())
                .orderType(order.getType())
                .amountOfTrade(order.getAmountOfTrade())
                .openPrice(order.getOpenPrice())
                .openTime(order.getOpenTime())
                .liquidationPrice(order.getLiquidationPrice())
                .leverage(order.getLeverage())
                .status(SimulationOrderResDto.Status.OPEN_NOW)
                .build();
    }

    public SimulationOrderResDto toModelOpenOrder(SimulationOrder order, OrderReport report, TimeReqDto dto) {
        Assert.isTrue(SimulationOrder.Status.OPEN == order.getStatus(), "Status order must be OPEN");
        return SimulationOrderResDto.builder()
                .requestInfo(buildRequestInfo(dto))
                .orderType(order.getType())
                .amountOfTrade(order.getAmountOfTrade())
                .simulationId(order.getSimulationId())
                .orderId(order.getId())
                .status(mappingStatus(order.getStatus(), ReportUtils.isProfit(report)))
                .openPrice(order.getOpenPrice())
                .closePrice(order.getClosePrice())
                .openTime(order.getOpenTime())
                .closeTime(order.getCloseTime())
                .leverage(order.getLeverage())
                .liquidationPrice(order.getLiquidationPrice())
                .profitLoss(report.getProfitLoss())
                .profitLossMinusFees(report.getProfitLossMinusFees())
                .percentageChange(report.getPercentageChange())
                .maxUnrealizedLossDuringTrade(report.getMaxUnrealizedLossDuringTrade())
                .maxUnrealizedProfitDuringTrade(report.getMaxUnrealizedProfitDuringTrade())
                .durationOfTradeInSeconds(report.getDurationOfTradeInSeconds())
                .percentageChange(report.getPercentageChange())
                .maxPriceDuringTrade(report.getMaxPriceDuringTrade())
                .minPriceDuringTrade(report.getMinPriceDuringTrade())
                .build();
    }

    public SimulationOrderResDto toModelCloseOrder(SimulationOrder order, TimeReqDto dto) {
        Assert.isTrue(SimulationOrder.Status.CLOSED == order.getStatus() || SimulationOrder.Status.LIQUIDATED == order.getStatus(), "Status order cannot be OPEN");
        return SimulationOrderResDto.builder()
                .requestInfo(buildRequestInfo(dto))
                .orderType(order.getType())
                .amountOfTrade(order.getAmountOfTrade())
                .simulationId(order.getSimulationId())
                .orderId(order.getId())
                .status(mappingStatus(order.getStatus(), ReportUtils.isProfit(order)))
                .openPrice(order.getOpenPrice())
                .closePrice(order.getClosePrice())
                .openTime(order.getOpenTime())
                .closeTime(order.getCloseTime())
                .leverage(order.getLeverage())
                .liquidationPrice(order.getLiquidationPrice())
                .profitLoss(order.getProfitLoss())
                .profitLossMinusFees(order.getProfitLossMinusFees())
                .percentageChange(order.getPercentageChange())
                .maxUnrealizedLossDuringTrade(order.getMaxUnrealizedLossDuringTrade())
                .maxUnrealizedProfitDuringTrade(order.getMaxUnrealizedProfitDuringTrade())
                .durationOfTradeInSeconds(order.getDurationOfTradeInSeconds())
                .percentageChange(order.getPercentageChange())
                .maxPriceDuringTrade(order.getMaxPriceDuringTrade())
                .minPriceDuringTrade(order.getMinPriceDuringTrade())
                .build();
    }


    public CloseSimulationResDto toModelClose(Map<SimulationOrder.Status, List<SimulationOrderResDto>> orders, List<SimulationEvent> events) {
        //todo
        return null;
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private String buildRequestInfo(TimeReqDto dto) {
        return dto.isRealTimeRequest() ? "REAL-TIME-REQUEST" : "BACK-TIME-REQUEST " + dto.getStartTime();
    }

    private SimulationOrderResDto.Status mappingStatus(SimulationOrder.Status orderStatus, Boolean isProfit) {
        if (orderStatus == SimulationOrder.Status.LIQUIDATED) {
            return SimulationOrderResDto.Status.LIQUIDATED;
        }
        return switch (orderStatus) {
            case OPEN -> mappingStatus(isProfit, SimulationOrderResDto.Status.OPEN_WITH_PROFIT, SimulationOrderResDto.Status.OPEN_WITH_LOSS, SimulationOrderResDto.Status.OPEN_NEUTRAL);
            case CLOSED -> mappingStatus(isProfit, SimulationOrderResDto.Status.CLOSED_WITH_PROFIT, SimulationOrderResDto.Status.CLOSED_WITH_LOSS, SimulationOrderResDto.Status.CLOSED_NEUTRAL);
            default -> throw new CustomException(ExceptionEnum.STATUS_NOT_ALLOWED_FOR_THIS_METHOD);
        };
    }

    private SimulationOrderResDto.Status mappingStatus(Boolean isProfit, SimulationOrderResDto.Status profitStatus, SimulationOrderResDto.Status lossStatus, SimulationOrderResDto.Status neutralStatus) {
        if (Boolean.TRUE.equals(isProfit)) {
            return profitStatus;
        } else if (Boolean.FALSE.equals(isProfit)) {
            return lossStatus;
        } else {
            return neutralStatus;
        }
    }
}
