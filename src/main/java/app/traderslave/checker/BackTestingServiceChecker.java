package app.traderslave.checker;

import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.model.domain.Simulation;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BackTestingServiceChecker {

    public void checkBalance(Simulation simulation, CreateSimulationOrderReqDto reqDto) {
        if (simulation.getBalance().compareTo(reqDto.getAmountOfTrade()) < 0) {
            throw new CustomException(ExceptionEnum.INSUFFICIENT_BALANCE);
        }
    }
}
