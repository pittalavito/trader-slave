package app.traderslave.bot.agent.assembler;

import app.traderslave.bot.agent.dto.MarketDataAgentDto;
import app.traderslave.model.dto.CandleDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BackTestMarketDataAssembler {

    public MarketDataAgentDto toModel(List<CandleDto> candles) {
        return MarketDataAgentDto.builder()
                .candles(candles)
                .build();
    }
}
