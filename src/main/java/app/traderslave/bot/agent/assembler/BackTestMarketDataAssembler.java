package app.traderslave.bot.agent.assembler;

import app.traderslave.bot.agent.model.MarketDataModel;
import app.traderslave.model.dto.CandleDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BackTestMarketDataAssembler {

    public MarketDataModel toModel(List<CandleDto> candles) {
        return MarketDataModel.builder()
                .candles(candles)
                .build();
    }
}
