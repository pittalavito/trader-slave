package app.traderslave.checker;

import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.SOrderStatus;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;

@UtilityClass
public class TestingChecker {

    public void checkOrderStatusOpen(SimulationOrder order) {
        if (SOrderStatus.OPEN != order.getStatus()) {
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

    private boolean isBalanceZero(Simulation simulation) {
        return simulation.getBalance().compareTo(BigDecimal.ZERO) <= 0;
    }

    private boolean isExcessiveAmount(Simulation simulation, CreateSimulationOrderReqDto reqDto) {
        return reqDto.getAmountOfTrade() != null && simulation.getBalance().compareTo(reqDto.getAmountOfTrade()) < 0;
    }
}
