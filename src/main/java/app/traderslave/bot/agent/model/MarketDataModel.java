package app.traderslave.bot.agent.model;

import app.traderslave.model.dto.CandleDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MarketDataModel {

    List<CandleDto> candles;

}