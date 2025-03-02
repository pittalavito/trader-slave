package app.traderslave.factory;

import app.traderslave.controller.dto.CreateSimulationReqDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class SimulationEntityFactory {

    public Simulation create(CreateSimulationReqDto dto) {
        return Simulation.builder()
                .currencyPair(dto.getCurrencyPair())
                .currency(dto.getCurrency())
                .balance(dto.getCurrency().getDefaultCapital())
                .uid(UUID.randomUUID().toString())
                .version(0)
                .build();
    }

    public Simulation subtractBalance(Simulation simulation, SimulationOrder order) {
        simulation.setBalance(simulation.getBalance().subtract(order.getAmountOfTrade()));
        simulation.setVersion(simulation.getVersion() + 1);
        simulation.setLastModificationDate(LocalDateTime.now());
        return simulation;
    }

    public Simulation addBalance(Simulation simulation, SimulationOrder order) {
        BigDecimal amountOfTradePlusProfit = order.getAmountOfTrade().add(order.getProfitLossMinusFees());

        simulation.setBalance(simulation.getBalance().add(amountOfTradePlusProfit));
        simulation.setVersion(simulation.getVersion() + 1);
        simulation.setLastModificationDate(LocalDateTime.now());
        return simulation;
    }
}
