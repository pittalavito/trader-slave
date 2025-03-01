package app.traderslave.checker;

import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.model.domain.Simulation;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;

@UtilityClass
public class BalanceChecker {

    public void checkBalance(Simulation simulation, CreateSimulationOrderReqDto reqDto) {
        if (isBalanceZero(simulation) || isExcessiveAmount(simulation, reqDto)) {
            throw new CustomException(ExceptionEnum.INSUFFICIENT_BALANCE);
        }
    }

    private boolean isBalanceZero(Simulation simulation) {
        return simulation.getBalance().compareTo(BigDecimal.ZERO) <= 0;
    }

    private boolean isExcessiveAmount(Simulation simulation, CreateSimulationOrderReqDto reqDto) {
        return reqDto.getAmountOfTrade() != null && simulation.getBalance().compareTo(reqDto.getAmountOfTrade()) < 0;
    }
}
