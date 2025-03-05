package app.traderslave.model.report;


import app.traderslave.controller.dto.SimulationOrderResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSimulation {
    private List<SimulationOrderResDto> openOrders;
    private List<SimulationOrderResDto> closeOrders;

    private BigDecimal balance;
    private Long simulationId;
}
