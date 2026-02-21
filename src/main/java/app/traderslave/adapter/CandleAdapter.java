package app.traderslave.adapter;

import app.traderslave.model.dto.SimulationPatterStrategyDto;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class CandleAdapter {

    public CandlesReqDto adapt(SimulationPatterStrategyDto dto) {
        var candles = new CandlesReqDto();
        candles.setCurrencyPair(dto.getCurrencyPair());
        candles.setStartTime(dto.getSimulationStartTime());
        candles.setEndTime(dto.getSimulationEndTime());
        candles.setTimeFrame(dto.getPatternDetectionTimeFrame());
        return candles;
    }

    public CandlesReqDto adapt(LocalDateTime startTime, CurrencyPair currencyPair, LocalDateTime endTime, TimeFrame timeFrame) {
        CandlesReqDto reqDto = new CandlesReqDto();
        reqDto.setCurrencyPair(currencyPair);
        reqDto.setStartTime(startTime);
        reqDto.setEndTime(endTime);
        reqDto.setTimeFrame(timeFrame);
        return reqDto;
    }

}