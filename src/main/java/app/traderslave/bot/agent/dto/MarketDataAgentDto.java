package app.traderslave.bot.agent.dto;

import app.traderslave.model.dto.CandleDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MarketDataAgentDto {

    List<CandleDto> candles;

}