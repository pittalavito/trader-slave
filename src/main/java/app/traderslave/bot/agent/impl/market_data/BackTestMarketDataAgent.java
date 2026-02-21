package app.traderslave.bot.agent.impl.market_data;

import app.traderslave.adapter.CandleAdapter;
import app.traderslave.bot.agent.assembler.BackTestMarketDataAssembler;
import app.traderslave.bot.BotConfig;
import app.traderslave.bot.agent.dto.MarketDataAgentDto;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.remote.service.BinanceRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestMarketDataAgent extends BaseMarketDataAgent<CandlesReqDto> {

    private final BinanceRemoteService binanceRemoteService;
    private final BackTestMarketDataAssembler marketDataService;
    private final CandleAdapter candleAdapter;

    @Override
    public MarketDataAgentDto get(CandlesReqDto request) {
        var candles = binanceRemoteService.findCandlesSync(request);
        return marketDataService.toModel(candles);
    }

    public CandlesReqDto adapt(BotConfig config) {
        return candleAdapter.adapt(
                config.getStartTime(),
                config.getCurrencyPair(),
                config.getStartTime().plusSeconds(config.getTimeFrameAnalysis().getSecond()),
                config.getTimeFrameAnalysis()
        );
    }

    public CandlesReqDto adapt(BotConfig config, LocalDateTime currentTime) {
        return candleAdapter.adapt(
                currentTime,
                config.getCurrencyPair(),
                currentTime.plusSeconds(config.getTimeFrameAnalysis().getSecond()),
                config.getTimeFrameAnalysis()
        );
    }
}
