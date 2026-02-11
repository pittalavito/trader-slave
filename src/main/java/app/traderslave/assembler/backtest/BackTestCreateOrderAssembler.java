package app.traderslave.assembler.backtest;

import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.model.dto.BackTestOrderDto;
import app.traderslave.model.dto.req.TimeReqDto;
import org.springframework.stereotype.Component;

@Component
public class BackTestCreateOrderAssembler {

    public BackTestOrderDto toModel(BackTestOrder order, TimeReqDto dto) {
        return BackTestOrderDto.builder()
                .requestInfo(buildRequestInfo(dto))
                .orderId(order.getId())
                .simulationId(order.getPortfolioId())
                .orderType(order.getType())
                .amountOfTrade(order.getAmountOfTrade())
                .openPrice(order.getOpenPrice())
                .openTime(order.getOpenTime())
                .liquidationPrice(order.getLiquidationPrice())
                .leverage(order.getLeverage())
                .status(BackTestOrderDto.Status.OPEN_NOW)
                .build();
    }

    private String buildRequestInfo(TimeReqDto dto) {
        return dto.isRealTimeRequest() ? "REAL-TIME-REQUEST" : "BACK-TIME-REQUEST " + dto.getStartTime();
    }
}
