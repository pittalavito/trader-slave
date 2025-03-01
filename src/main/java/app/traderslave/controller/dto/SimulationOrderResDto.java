package app.traderslave.controller.dto;

import app.traderslave.model.domain.ReportOrder;
import app.traderslave.model.enums.OrderType;
import app.traderslave.model.enums.SOrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimulationOrderResDto {
    private Long id;
    private Long simulationId;
    private OrderType orderType;
    private BigDecimal amountOfTrade;
    private BigDecimal openPrice;
    private LocalDateTime openTime;
    private BigDecimal balance;
    private BigDecimal liquidationPrice;
    private Integer leverage;
    private SOrderStatus status;
    private ReportOrder report;
}
