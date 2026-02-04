package app.traderslave.adapter;

import app.traderslave.controller.dto.*;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class CandlesReqDtoAdapter {

    public CandleReqDto adapt(CurrencyPair currencyPair, CreateSimulationOrderReqDto dto) {
        CandleReqDto reqDto = new CandleReqDto();
        reqDto.setCurrencyPair(currencyPair);
        reqDto.setStartTime(dto.getStartTime());
        reqDto.setRealTimeRequest(dto.isRealTimeRequest());
        return reqDto;
    }

    public CandlesReqDto adapt(LocalDateTime openTime, CurrencyPair currencyPair, LocalDateTime endTime, TimeFrame timeFrame) {
        CandlesReqDto reqDto = new CandlesReqDto();
        reqDto.setCurrencyPair(currencyPair);
        reqDto.setStartTime(openTime);
        reqDto.setEndTime(endTime);
        reqDto.setTimeFrame(timeFrame);
        return reqDto;
    }
}
