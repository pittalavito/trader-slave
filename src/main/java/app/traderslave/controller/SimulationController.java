package app.traderslave.controller;

import app.traderslave.command.SimulationPatternStrategyCommand;
import app.traderslave.controller.dto.*;
import app.traderslave.service.simulation.SimulationService;
import app.traderslave.utility.ControllerPath;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping(ControllerPath.SIMULATION)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationController {

    private static final String URI_ORDER = "/order";
    private static final String URI_ALL = "/all";
    private static final String URI_PATTERN_STRATEGY = "/pattern-strategy";

    private final SimulationService simulationService;
    private final SimulationPatternStrategyCommand simulationPatternStrategyCommand;

    // --- SIMULATION --------------------------------------------------------------------------------------------------

    @PostMapping
    public Mono<ResponseEntity<CreateSimulationResDto>> create(@RequestBody @Validated CreateSimulationReqDto dto) {
        return simulationService.create(dto)
                .map(ResponseEntity::ok);
    }

    @PutMapping
    public Mono<ResponseEntity<CloseSimulationResDto>> close(@RequestBody @Validated CloseSimulationReqDto dto) {
        return simulationService.close(dto)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = URI_PATTERN_STRATEGY)
    public Mono<ResponseEntity<CloseSimulationResDto>> simulationPatternStrategy() throws InterruptedException {
        return simulationPatternStrategyCommand.execute()
                .map(ResponseEntity::ok);
    }

    @Transactional
    @DeleteMapping(path = URI_ALL)
    public void deleteAll() {
        simulationService.deleteAll();
    }

    // --- ORDER -------------------------------------------------------------------------------------------------------

    @PostMapping(path = URI_ORDER)
    public Mono<ResponseEntity<SimulationOrderResDto>> createOrder(@RequestBody @Validated CreateSimulationOrderReqDto dto) {
        return simulationService.createOrder(dto)
                .map(ResponseEntity::ok);
    }

    @PutMapping(path = URI_ORDER)
    public Mono<ResponseEntity<SimulationOrderResDto>> closeOrder(@RequestBody @Validated CloseSimulationOrderReqDto dto) {
        return simulationService.closeOrder(dto)
                .map(ResponseEntity::ok);
    }
}
