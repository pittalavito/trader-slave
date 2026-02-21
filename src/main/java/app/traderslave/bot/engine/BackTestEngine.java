package app.traderslave.bot.engine;

import app.traderslave.bot.BotConfig;
import app.traderslave.bot.EngineCloseOrderFlow;
import app.traderslave.bot.EngineOpenOrderFlow;
import app.traderslave.bot.agent.impl.market_data.BackTestMarketDataAgent;
import app.traderslave.bot.agent.impl.order_executor.BaseOrderExecutorAgent;
import app.traderslave.bot.agent.impl.portfolio.BackTestPortfolioAgent;
import app.traderslave.bot.agent.impl.position.BasePositionSizerAgent;
import app.traderslave.bot.agent.impl.risk.BackTestRiskAgent;
import app.traderslave.bot.agent.dto.RiskAgentReqDto;
import app.traderslave.bot.agent.impl.signal.DetectPatternsSignalAgent;
import app.traderslave.bot.manager.BackTestBotManager;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.dto.req.CandlesReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Scope("prototype")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class BackTestEngine extends BaseEngine {

    private final BackTestBotManager botManager;

    private final BackTestMarketDataAgent marketDataAgent;
    //todo prende
    private final BaseOrderExecutorAgent<Void, Void> orderExecutorAgent;
    private final BackTestPortfolioAgent portfolioAgent;
    //todo in base al rischio, config, portfofio balance e forse altro calcola leva e size della posizione
    private final BasePositionSizerAgent<Void, Void> positionSizerAgent;
    private final DetectPatternsSignalAgent signalAgent;
    private final BackTestRiskAgent riskAgent;

    //todo dati usati at catzum attualmente
    private Long botId;
    private Map<LocalDateTime, CandleDto> marketDataCache;
    private CandlesReqDto marketDataRequest;

    @Override
    public void init(BotConfig config) {
        marketDataCache = new HashMap<>();
        portfolioAgent.create(config);
        //todo passare portfolioID
        botId = botManager.start(config);
    }

    @Override
    public void execute(Long botId) {
        var bot = botManager.get(botId);
        var marketData = marketDataAgent.get(marketDataRequest);
        var portfolio = portfolioAgent.get(bot.getPortfolioId());

        var riskRequest = new RiskAgentReqDto(marketData, portfolio, bot.getConfig());
        var risk = riskAgent.calculate(riskRequest);

        var signal = signalAgent.generate(marketData);

        if (risk.canOpenOrder()) {
            var flowRequest = new EngineOpenOrderFlow(signal, portfolio, risk);
            openOrderFlow(flowRequest);
        }

        var flowRequest = new EngineCloseOrderFlow(marketData, portfolio, risk, signal);
        closeOrderFlow(flowRequest);
    }

    @Override
    public void openOrderFlow(EngineOpenOrderFlow flow) {
        if (flow.noSignal()) {
            return;
        }
        log.info("Executing open order flow for portfolio {}", flow.portfolio().getId());
    }

    @Override
    public void closeOrderFlow(EngineCloseOrderFlow flow) {
        if (flow.noOpenOrders()) {
           return;
        }
        log.info("Executing close order flow for portfolio {}", flow.portfolio().getId());
    }

}
