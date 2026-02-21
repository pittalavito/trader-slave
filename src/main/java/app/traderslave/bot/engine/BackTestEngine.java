package app.traderslave.bot.engine;

import app.traderslave.bot.BotConfig;
import app.traderslave.bot.agent.impl.market_data.BackTestMarketDataAgent;
import app.traderslave.bot.agent.impl.portfolio.BackTestPortfolioAgent;
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

    private static final int THREAD_SLEEP_TIME_MS = 1000;

    private final BackTestBotManager botManager;

    private final BackTestPortfolioAgent portfolioAgent;
    private final BackTestMarketDataAgent marketDataAgent;
    private final DetectPatternsSignalAgent signalAgent;

    //private final TradingSignals tradingSignals;
    //private final RiskManager riskManager;
    //private final PositionSizer positionSizer;
    //private final OrderExecutor orderExecutor;

    private Long botId;
    private Map<LocalDateTime, CandleDto> marketDataCache;
    private CandlesReqDto marketDataRequest;

    @Override
    public void init(BotConfig config) {
        marketDataCache = new HashMap<>();
        botId = botManager.start(config);
    }

    @Override
    public void execute(Long botId) {
        var bot = botManager.get(botId);
        var portfolio = portfolioAgent.get(bot.getPortfolioId());
        var marketData = marketDataAgent.get(marketDataRequest);
    }

}
