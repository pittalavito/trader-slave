package app.traderslave.adapter.backtest;

import app.traderslave.model.dto.SimulationPatterStrategyDto;
import app.traderslave.model.dto.req.BackTestCreatePortfolioReqDto;
import org.springframework.stereotype.Component;

@Component
public class BackTestCreatePortfolioAdapter {

    public BackTestCreatePortfolioReqDto adapt(SimulationPatterStrategyDto dto) {
        var createSimulation = new BackTestCreatePortfolioReqDto();
        createSimulation.setCurrencyPair(dto.getCurrencyPair());
        createSimulation.setStartTime(dto.getSimulationStartTime());
        createSimulation.setDescription("Pattern Strategy Simulation");
        return createSimulation;
    }
}
