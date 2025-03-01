package app.traderslave.adapter;

import app.traderslave.controller.dto.CandleReqDto;
import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class BinanceServiceAdapter {

    public CandleReqDto adapt(CurrencyPair currencyPair, LocalDateTime time) {
        CandleReqDto reqDto = new CandleReqDto();
        reqDto.setCurrencyPair(currencyPair);
        reqDto.setTime(time);
        return reqDto;
    }

    public CandlesReqDto adapt(SimulationOrder order, Simulation simulation, LocalDateTime time) {
        CandlesReqDto reqDto = new CandlesReqDto();
        reqDto.setCurrencyPair(simulation.getCurrencyPair());
        reqDto.setStartDate(order.getOpenTime());
        reqDto.setEndDate(time);
        reqDto.setTimeFrame(TimeFrame.ONE_MINUTE);
        return reqDto;
    }
}
