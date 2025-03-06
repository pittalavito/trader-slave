package app.traderslave.assembler;

import app.traderslave.controller.dto.*;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.SOrderStatus;
import app.traderslave.model.report.OrderReport;
import app.traderslave.service.SimulationService;
import app.traderslave.utility.ReportUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SimulationServiceAssembler {

    /**
     * For response of {@link SimulationService#create(CreateSimulationReqDto)}}
     */
    public PostSimulationResDto toModel(Simulation simulation) {
        return PostSimulationResDto.builder()
                .simulationId(simulation.getId())
                .balance(simulation.getBalance())
                .currency(simulation.getCurrency())
                .build();
    }

    /**
     * For response of {@link SimulationService#createOrder(CreateSimulationOrderReqDto)}
     */
    public SimulationOrderResDto toModel(SimulationOrder order, TimeReqDto dto) {
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

    /**
     * For response of :
     *  - {@link SimulationService#retrieveOrder(GetSimulationOrderReqDto)}
     *  - {@link SimulationService#closeOrder(CloseSimulationOrderReqDto)}
     */
    public SimulationOrderResDto toModel(SimulationOrder order, OrderReport report, TimeReqDto dto) {
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

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private String buildRequestInfo(TimeReqDto dto) {
        return dto.isRealTimeRequest() ? "REAL-TIME-REQUEST" : "BACK-TIME-REQUEST " + dto.getStartTime();
    }

    private SimulationOrderResDto.Status mappingStatus(SOrderStatus orderStatus, Boolean isProfit) {
        if (orderStatus == SOrderStatus.LIQUIDATED) {
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
