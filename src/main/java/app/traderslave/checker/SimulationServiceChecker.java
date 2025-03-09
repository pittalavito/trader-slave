package app.traderslave.checker;

import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.controller.dto.TimeReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationEvent;
import app.traderslave.model.domain.SimulationOrder;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;

@UtilityClass
public class SimulationServiceChecker {

    public void checkSimulationStatusOpen(Simulation simulation) {
        if (!simulation.isOpen()) {
            throw new CustomException(ExceptionEnum.SIMULATION_STATUS_IS_NOT_OPEN);
        }
    }

    public void checkRequestTime(Simulation simulation, SimulationEvent latestEvent, TimeReqDto dto) {
        if(dto.getStartTime() == null || dto.isRealTimeRequest()) {
           return;
        }
        if(dto.getStartTime().isBefore(simulation.getStartTime())) {
            throw new CustomException(ExceptionEnum.START_TIME_IS_BEFORE_SIMULATION_START_TIME);
        }
        if (latestEvent != null && dto.getStartTime().isBefore(latestEvent.getEventTime())) {
            throw new CustomException(ExceptionEnum.START_TIME_IS_BEFORE_LATEST_EVENT_TIME);
        }
    }

    public void checkOrderStatusOpen(SimulationOrder order) {
        if (!order.isOpen()) {
            throw new CustomException(ExceptionEnum.ORDER_STATUS_IS_NOT_OPEN);
        }
    }

    public void checkAmountOfTrade(CreateSimulationOrderReqDto reqDto) {
        if (!reqDto.isMaxAmountOfTrade() && (reqDto.getAmountOfTrade() == null || reqDto.getAmountOfTrade().doubleValue() <= 0)) {
            throw new CustomException(ExceptionEnum.AMOUNT_OF_TRADE_INVALID);
        }
    }

    public void checkBalance(Simulation simulation, CreateSimulationOrderReqDto reqDto) {
        if (isBalanceZero(simulation) || isExcessiveAmount(simulation, reqDto)) {
            throw new CustomException(ExceptionEnum.INSUFFICIENT_BALANCE);
        }
    }

    public void checkLeverage(CreateSimulationOrderReqDto reqDto) {
        if (reqDto.getLeverage() < 1 || reqDto.getLeverage() > 100) {
            throw new CustomException(ExceptionEnum.INVALID_LEVERAGE);
        }
    }

    private boolean isBalanceZero(Simulation simulation) {
        return simulation.getBalance().compareTo(BigDecimal.ZERO) <= 0;
    }

    private boolean isExcessiveAmount(Simulation simulation, CreateSimulationOrderReqDto reqDto) {
        return reqDto.getAmountOfTrade() != null && simulation.getBalance().compareTo(reqDto.getAmountOfTrade()) < 0;
    }
}
