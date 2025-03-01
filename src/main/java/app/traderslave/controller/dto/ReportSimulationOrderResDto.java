package app.traderslave.controller.dto;

import app.traderslave.model.enums.OrderType;
import app.traderslave.model.enums.SOrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportSimulationOrderResDto {
    private Long id;
    private OrderType orderType;
    private SOrderStatus status;
    private String time;
    private String closeTime;
    private String openPrice;
    private String closePrice;
    private String quantity;
    private String profit;

}
