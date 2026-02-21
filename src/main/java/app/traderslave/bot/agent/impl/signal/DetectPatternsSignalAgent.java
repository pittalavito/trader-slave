package app.traderslave.bot.agent.impl.signal;

import app.traderslave.bot.agent.dto.DetectPatternSignalAgentDto;
import app.traderslave.bot.agent.dto.MarketDataAgentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DetectPatternsSignalAgent extends BaseSignalAgent<DetectPatternSignalAgentDto> {

    @Override
    public DetectPatternSignalAgentDto generate(MarketDataAgentDto request) {
        //todo implement
        return null;
    }
}
