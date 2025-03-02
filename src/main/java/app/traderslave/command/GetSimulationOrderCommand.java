package app.traderslave.command;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.assembler.TestingServiceAssembler;
import app.traderslave.controller.dto.*;
import app.traderslave.factory.ReportOrderFactory;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.SOrderStatus;
import app.traderslave.model.report.ReportOrder;
import app.traderslave.service.BinanceService;
import app.traderslave.service.SimulationOrderService;
import app.traderslave.service.SimulationService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Scope("prototype")
public class GetSimulationOrderCommand extends BaseMonoCommand<GetSimulationOrderReqDto, SimulationOrderResDto> {

    private final BinanceService binanceService;
    private final SimulationService simulationService;
    private final SimulationOrderService simulationOrderService;

    protected GetSimulationOrderCommand(GetSimulationOrderReqDto requestDto, BinanceService binanceService, SimulationService simulationService, SimulationOrderService simulationOrderService) {
        this.requestDto = requestDto;
        this.binanceService = binanceService;
        this.simulationService = simulationService;
        this.simulationOrderService = simulationOrderService;
    }

    @Override
    public Mono<SimulationOrderResDto> execute() {
        Simulation simulation = simulationService.findByIdOrError(requestDto.getSimulationId());
        SimulationOrder order = simulationOrderService.findByIdAndSimulationIdOrError(requestDto.getOrderId(), requestDto.getSimulationId());

        if (SOrderStatus.OPEN == order.getStatus()) {
            CandlesReqDto candleReqDto = BinanceServiceAdapter.adapt(order, simulation, requestDto);
            return binanceService.findCandles(candleReqDto)
                    .map(candles -> responseOpenOrder(order, candles, simulation));
        }

        return Mono.just(responseNotOpenOrder(order, simulation));
    }

    private SimulationOrderResDto responseOpenOrder(SimulationOrder order, CandlesResDto candles, Simulation simulation) {
        ReportOrder report = ReportOrderFactory.create(order, candles);
        return TestingServiceAssembler.toModel(order, simulation, report, requestDto);
    }

    private SimulationOrderResDto responseNotOpenOrder(SimulationOrder order, Simulation simulation) {
        ReportOrder report = ReportOrderFactory.create(order);
        return TestingServiceAssembler.toModel(order, simulation, report, requestDto);
    }
}
