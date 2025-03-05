package app.traderslave.controller.dto;

import app.traderslave.model.report.AiReportSimulation;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Data
@SuperBuilder
public class CloseSimulationResDto {
    private String requestInfo;
    private Long simulationId;
    private List<SimulationOrderResDto> openOrders;
    private List<SimulationOrderResDto> closeOrders;
    private List<SimulationOrderResDto> liquidateOrders;
    private AiReportSimulation report;
}
