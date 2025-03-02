package app.traderslave.controller.dto;

import app.traderslave.model.enums.OrderType;
import app.traderslave.model.enums.SOrderStatus;
import app.traderslave.model.report.AiReportOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimulationOrderResDto {
    private Long simulationId;
    private Long orderId;
    private OrderType orderType;
    private SOrderStatus status;
    private BigDecimal balance;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Integer leverage;
    private BigDecimal amountOfTrade;
    private BigDecimal liquidationPrice;
    private AiReportOrder report;
}
