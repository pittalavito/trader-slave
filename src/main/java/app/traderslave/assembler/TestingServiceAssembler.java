package app.traderslave.assembler;

import app.traderslave.controller.dto.SimulationOrderResDto;
import app.traderslave.controller.dto.PostSimulationResDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class TestingServiceAssembler {

    public PostSimulationResDto toModel(Simulation simulation) {
        return PostSimulationResDto.builder()
                        .simulationId(simulation.getId())
                        .balance(simulation.getBalance())
                        .currency(simulation.getCurrency())
                        .build();
    }

    public SimulationOrderResDto toModel(SimulationOrder simulationOrder, BigDecimal balance) {
        return SimulationOrderResDto.builder()
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

    public SimulationOrderResDto toModel(SimulationOrder simulationOrder, Simulation simulation) {
        return SimulationOrderResDto.builder()
                .orderType(simulationOrder.getType())
                .amountOfTrade(simulationOrder.getAmountOfTrade())
                .simulationId(simulation.getId())
                .orderId(simulationOrder.getId())
                .balance(simulation.getBalance())
                .status(simulationOrder.getStatus())
                .openPrice(simulationOrder.getOpenPrice())
                .closePrice(simulationOrder.getReport().getClosePrice())
                .openTime(simulationOrder.getOpenTime())
                .closeTime(LocalDateTime.parse(simulationOrder.getReport().getCloseTime()))
                .leverage(simulationOrder.getLeverage())
                .profitLoss(simulationOrder.getReport().getProfitLoss())
                .profitLossMinusFees(simulationOrder.getReport().getProfitLossMinusFees())
                .percentageChange(simulationOrder.getReport().getPercentageChange())
                .maxUnrealizedProfitDuringTrade(simulationOrder.getReport().getMaxUnrealizedProfitDuringTrade())
                .maxUnrealizedLossDuringTrade(simulationOrder.getReport().getMaxUnrealizedLossDuringTrade())
                .durationOfTradeInSeconds(simulationOrder.getReport().getDurationOfTradeInSeconds())
                .liquidationPrice(simulationOrder.getLiquidationPrice())
                .build();
    }
}
