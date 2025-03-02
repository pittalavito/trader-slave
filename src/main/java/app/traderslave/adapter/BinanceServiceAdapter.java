package app.traderslave.adapter;

import app.traderslave.controller.dto.*;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BinanceServiceAdapter {

    public CandleReqDto adapt(CurrencyPair currencyPair, CreateSimulationOrderReqDto dto) {
        CandleReqDto reqDto = new CandleReqDto();
        reqDto.setCurrencyPair(currencyPair);
        reqDto.setTime(dto.getTime());
        reqDto.setRealTimeRequest(dto.isRealTimeRequest());
        return reqDto;
    }

    public CandlesReqDto adapt(SimulationOrder order, Simulation simulation, SimulationOrderReqDto dto) {
        CandlesReqDto reqDto = new CandlesReqDto();
        reqDto.setCurrencyPair(simulation.getCurrencyPair());
        reqDto.setStartDate(order.getOpenTime());
        reqDto.setEndDate(dto.getTime());
        reqDto.setTimeFrame(TimeFrame.ONE_MINUTE);
        reqDto.setRealTimeRequest(dto.isRealTimeRequest());
        return reqDto;
    }
}
