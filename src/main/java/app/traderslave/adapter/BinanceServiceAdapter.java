package app.traderslave.adapter;

import app.traderslave.checker.BinanceServiceChecker;
import app.traderslave.controller.dto.*;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class BinanceServiceAdapter {

    public CandleReqDto adapt(CurrencyPair currencyPair, CreateSimulationOrderReqDto dto) {
        CandleReqDto reqDto = new CandleReqDto();
        reqDto.setCurrencyPair(currencyPair);
        reqDto.setStartTime(dto.getStartTime());
        reqDto.setRealTimeRequest(dto.isRealTimeRequest());
        return reqDto;
    }

    public CandlesReqDto adapt(LocalDateTime openTime, Simulation simulation, LocalDateTime endTime, TimeFrame timeFrame) {
        CandlesReqDto reqDto = new CandlesReqDto();
        reqDto.setCurrencyPair(simulation.getCurrencyPair());
        reqDto.setStartTime(openTime);
        reqDto.setEndTime(endTime);
        reqDto.setTimeFrame(timeFrame);
        reqDto.setLastNumCandle(BinanceServiceChecker.LIMIT_NUM_CANDLES);
        return reqDto;
    }
}
