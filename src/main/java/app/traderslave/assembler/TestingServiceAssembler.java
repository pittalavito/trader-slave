package app.traderslave.assembler;

import app.traderslave.controller.dto.SimulationOrderResDto;
import app.traderslave.controller.dto.PostSimulationResDto;
import app.traderslave.controller.dto.TimeReqDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.report.AiReportOrder;
import app.traderslave.model.report.ReportOrder;
import app.traderslave.utility.ReportUtils;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;

@UtilityClass
public class TestingServiceAssembler {

    /**
     * CREATE SIMULATION
     */
    public PostSimulationResDto toModel(Simulation simulation) {
        return PostSimulationResDto.builder()
                .simulationId(simulation.getId())
                .balance(simulation.getBalance())
                .currency(simulation.getCurrency())
                .build();
    }

    /**
     * CREATE SIMULATION ORDER
     */
    public SimulationOrderResDto toModel(SimulationOrder simulationOrder, BigDecimal balance, TimeReqDto dto) {
        return SimulationOrderResDto.builder()
                .requestInfo(buildRequestInfo(dto))
                .orderId(simulationOrder.getId())
                .simulationId(simulationOrder.getSimulationId())
                .orderType(simulationOrder.getType())
                .amountOfTrade(simulationOrder.getAmountOfTrade())
                .openPrice(simulationOrder.getOpenPrice())
                .openTime(simulationOrder.getOpenTime())
                .balance(balance)
                .liquidationPrice(simulationOrder.getLiquidationPrice())
                .leverage(simulationOrder.getLeverage())
                .status(simulationOrder.getStatus())
                .build();
    }

    /**
     * CLOSE SIMULATION ORDER AND GET SIMULATION ORDER
     */
    public SimulationOrderResDto toModel(SimulationOrder simulationOrder, Simulation simulation, ReportOrder report, TimeReqDto dto) {
        return SimulationOrderResDto.builder()
                .requestInfo(buildRequestInfo(dto))
                .orderType(simulationOrder.getType())
                .amountOfTrade(simulationOrder.getAmountOfTrade())
                .simulationId(simulation.getId())
                .orderId(simulationOrder.getId())
                .balance(simulation.getBalance())
                .status(simulationOrder.getStatus())
                .openPrice(simulationOrder.getOpenPrice())
                .closePrice(simulationOrder.getClosePrice())
                .openTime(simulationOrder.getOpenTime())
                .closeTime(simulationOrder.getCloseTime())
                .leverage(simulationOrder.getLeverage())
                .liquidationPrice(simulationOrder.getLiquidationPrice())
                .report(AiReportOrder.builder()
                        .status(ReportUtils.calculateAiStatusOrder(simulationOrder, report))
                        .profitLoss(report.getProfitLoss())
                        .profitLossMinusFees(report.getProfitLossMinusFees())
                        .percentageChange(report.getPercentageChange())
                        .maxUnrealizedLossDuringTrade(report.getMaxUnrealizedLossDuringTrade())
                        .maxUnrealizedProfitDuringTrade(report.getMaxUnrealizedProfitDuringTrade())
                        .durationOfTradeInSeconds(report.getDurationOfTradeInSeconds())
                        .percentageChange(report.getPercentageChange())
                        .build())
                .build();
    }

    private String buildRequestInfo(TimeReqDto dto) {
        return dto.isRealTimeRequest() ?
                "REAL-TIME-REQUEST" :
                "BACK-TIME-REQUEST " + dto.getStartTime();
    }
}
