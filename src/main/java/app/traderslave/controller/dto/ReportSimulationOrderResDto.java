package app.traderslave.controller.dto;

import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.OrderType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportSimulationOrderResDto {
    private Long id;
    private OrderType orderType;
    private SimulationOrder.Status status;
    private String time;
    private String closeTime;
    private String openPrice;
    private String closePrice;
    private String quantity;
    private String profit;

}
