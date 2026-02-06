package app.traderslave.adapter;

import app.traderslave.model.dto.SimulationPatterStrategyDto;
import app.traderslave.model.dto.req.CandlesReqDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CandleDtoAdapter {

    public CandlesReqDto adapt(SimulationPatterStrategyDto dto) {
        var candles = new CandlesReqDto();
        candles.setCurrencyPair(dto.getCurrencyPair());
        candles.setStartTime(dto.getSimulationStartTime());
        candles.setEndTime(dto.getSimulationEndTime());
        candles.setTimeFrame(dto.getPatternDetectionTimeFrame());
        return candles;
    }
}