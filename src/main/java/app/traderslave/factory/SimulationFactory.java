package app.traderslave.factory;

import app.traderslave.controller.dto.CreateSimulationReqDto;
import app.traderslave.controller.dto.TimeReqDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.utility.TimeUtils;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
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
                .description(dto.getDescription())
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
        simulation.setBalance(simulation.getBalance().subtract(order.getAmountOfTrade()));
        simulation.setVersion(simulation.getVersion() + 1);
        simulation.setLastModificationDate(LocalDateTime.now());
        return simulation;
    }

    public Simulation addBalance(Simulation simulation, SimulationOrder order) {
        BigDecimal amountOfTradePlusProfitLoss = order.getAmountOfTrade().add(order.getProfitLoss());

        simulation.setBalance(simulation.getBalance().add(amountOfTradePlusProfitLoss));
        simulation.setVersion(simulation.getVersion() + 1);
        simulation.setLastModificationDate(LocalDateTime.now());
        return simulation;
    }
}
