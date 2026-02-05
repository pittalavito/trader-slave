package app.traderslave.domain.factory;

import app.traderslave.controller.dto.CreateSimulationReqDto;
import app.traderslave.controller.dto.TimeReqDto;
import app.traderslave.domain.model.Simulation;
import app.traderslave.domain.model.SimulationOrder;
import app.traderslave.utils.TimeUtils;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class SimulationFactory {

    public Simulation create(CreateSimulationReqDto dto) {
        return Simulation.builder()
                .currencyPair(dto.getCurrencyPair())
                .currency(dto.getCurrency())
                .balance(dto.getCurrency().getDefaultCapital())
                .status(Simulation.Status.OPEN)
                .startTime(dto.getStartTime())
                .uid(UUID.randomUUID().toString())
                .creationDate(LocalDateTime.now())
                .description(dto.getDescription())
                .dataSource(dto.getDataSource())
                .version(0)
                .build();
    }

    public Simulation close(Simulation simulation, TimeReqDto dto) {
        simulation.setStatus(Simulation.Status.CLOSED);
        simulation.setVersion(simulation.getVersion() + 1);
        simulation.setLastModificationDate(LocalDateTime.now());
        simulation.setEndTime(dto.isRealTimeRequest() ? TimeUtils.now() : dto.getStartTime());
        return simulation;
    }

    public Simulation subtractBalance(Simulation simulation, SimulationOrder order) {
        BigDecimal newBalance = simulation.getBalance().subtract(order.getAmountOfTrade()).setScale(2, RoundingMode.HALF_UP);
        simulation.setBalance(newBalance);
        simulation.setVersion(simulation.getVersion() + 1);
        simulation.setLastModificationDate(LocalDateTime.now());
        return simulation;
    }

    public Simulation addBalance(Simulation simulation, SimulationOrder order) {
        BigDecimal amountOfTradePlusProfitLoss = order.getAmountOfTrade().add(order.getProfitLoss());
        BigDecimal newBalance = simulation.getBalance().add(amountOfTradePlusProfitLoss).setScale(2, RoundingMode.HALF_UP);

        simulation.setBalance(newBalance);
        simulation.setVersion(simulation.getVersion() + 1);
        simulation.setLastModificationDate(LocalDateTime.now());
        return simulation;
    }
}
