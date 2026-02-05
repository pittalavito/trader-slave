package app.traderslave.command.simulation;

import app.traderslave.command.base.BaseCommand;
import app.traderslave.model.dto.req.TimeReqDto;
import app.traderslave.domain.model.Simulation;
import app.traderslave.domain.model.SimulationOrder;
import app.traderslave.model.dto.OrderReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CreateSimulationOrderReportCommand extends BaseCommand<CreateSimulationOrderReportCommand.CommandRequest, OrderReportDto> {

    @Override
    public OrderReportDto execute() {
        // todo implkementare al posto di SimulationOrderReportManagerService
        return null;
    }

    public static class CommandRequest {
        private Simulation simulation;
        private SimulationOrder order;
        private TimeReqDto dto;
    }
}
