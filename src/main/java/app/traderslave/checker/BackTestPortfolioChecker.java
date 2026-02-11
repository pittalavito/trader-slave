package app.traderslave.checker;

import app.traderslave.model.dto.req.BackTestCreateOrderReqDto;
import app.traderslave.model.dto.req.TimeReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.model.BackTestPortfolioEvent;
import app.traderslave.domain.model.BackTestOrder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;

@Slf4j
@UtilityClass
public class BackTestPortfolioChecker {

    public void checkPortfolioStatusOpen(BackTestPortfolio backTestPortfolio) {
        if (!backTestPortfolio.isOpen()) {
            throw new CustomException(ExceptionEnum.SIMULATION_STATUS_IS_NOT_OPEN);
        }
    }

    public void checkRequestTime(BackTestPortfolio backTestPortfolio, BackTestPortfolioEvent latestEvent, TimeReqDto dto) {
        if(dto.getStartTime() == null || dto.isRealTimeRequest()) {
           return;
        }
        if(dto.getStartTime().isBefore(backTestPortfolio.getStartTime())) {
            throw new CustomException(ExceptionEnum.START_TIME_IS_BEFORE_SIMULATION_START_TIME);
        }
        if (latestEvent != null && dto.getStartTime().isBefore(latestEvent.getEventTime())) {
            log.error("Request start time {} is before latest event time {}", dto.getStartTime(), latestEvent.getEventTime());
            throw new CustomException(ExceptionEnum.START_TIME_IS_BEFORE_LATEST_EVENT_TIME);
        }
    }

    public void checkOrderStatusOpen(BackTestOrder order) {
        if (!order.isOpen()) {
            throw new CustomException(ExceptionEnum.ORDER_STATUS_IS_NOT_OPEN);
        }
    }

    public void checkAmountOfTrade(BackTestCreateOrderReqDto reqDto) {
        if (!reqDto.isMaxAmountOfTrade() && (reqDto.getAmountOfTrade() == null || reqDto.getAmountOfTrade().doubleValue() <= 0)) {
            throw new CustomException(ExceptionEnum.AMOUNT_OF_TRADE_INVALID);
        }
    }

    public void checkBalance(BackTestPortfolio backTestPortfolio, BackTestCreateOrderReqDto reqDto) {
        if (isBalanceZero(backTestPortfolio) || isExcessiveAmount(backTestPortfolio, reqDto)) {
            throw new CustomException(ExceptionEnum.INSUFFICIENT_BALANCE);
        }
    }

    public void checkLeverage(BackTestCreateOrderReqDto reqDto) {
        if (reqDto.getLeverage() < 1 || reqDto.getLeverage() > 100) {
            throw new CustomException(ExceptionEnum.INVALID_LEVERAGE);
        }
    }

    private boolean isBalanceZero(BackTestPortfolio backTestPortfolio) {
        return backTestPortfolio.getBalance().compareTo(BigDecimal.ZERO) <= 0;
    }

    private boolean isExcessiveAmount(BackTestPortfolio backTestPortfolio, BackTestCreateOrderReqDto reqDto) {
        return reqDto.getAmountOfTrade() != null && backTestPortfolio.getBalance().compareTo(reqDto.getAmountOfTrade()) < 0;
    }
}
