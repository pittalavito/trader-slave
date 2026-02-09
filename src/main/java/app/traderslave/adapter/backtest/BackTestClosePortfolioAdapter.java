package app.traderslave.adapter.backtest;

import app.traderslave.model.dto.req.BackTestClosePortfolioReqDto;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class BackTestClosePortfolioAdapter {

    public BackTestClosePortfolioReqDto adapt(Long simulationId, LocalDateTime localDateTime) {
        var closeSimulation = new BackTestClosePortfolioReqDto();
        closeSimulation.setSimulationId(simulationId);
        closeSimulation.setStartTime(localDateTime);
        return closeSimulation;
    }
}
