package app.traderslave.controller;

import app.traderslave.command.SimulationPatternStrategyCommand;
import app.traderslave.controller.dto.*;
import app.traderslave.service.manager.SimulationManagerService;
import app.traderslave.service.manager.SimulationOrderManagerService;
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

    private final SimulationManagerService simulationManagerService;
    private final SimulationOrderManagerService orderManagerService;
    private final SimulationPatternStrategyCommand simulationPatternStrategyCommand;

    // --- SIMULATION --------------------------------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<CreateSimulationResDto> create(@RequestBody @Validated CreateSimulationReqDto dto) {
        return ResponseEntity.ok(simulationManagerService.create(dto));
    }

    @PutMapping
    public ResponseEntity<CloseSimulationResDto> close(@RequestBody @Validated CloseSimulationReqDto dto) {
        return ResponseEntity.ok(simulationManagerService.close(dto));
    }

    @PostMapping(path = URI_PATTERN_STRATEGY)
    public ResponseEntity<CloseSimulationResDto> simulationPatternStrategy(@RequestBody @Validated SimulationPatterStrategyDto dto) throws InterruptedException {
        simulationPatternStrategyCommand.setRequestDto(dto);
        return ResponseEntity.ok(simulationPatternStrategyCommand.execute());
    }

    @Transactional
    @DeleteMapping(path = URI_ALL)
    public void deleteAll() {
        simulationManagerService.deleteAll();
    }

    // --- ORDER -------------------------------------------------------------------------------------------------------

    @PostMapping(path = URI_ORDER)
    public ResponseEntity<SimulationOrderResDto> createOrder(@RequestBody @Validated CreateSimulationOrderReqDto dto) {
        return ResponseEntity.ok(orderManagerService.create(dto));
    }

    @PutMapping(path = URI_ORDER)
    public ResponseEntity<SimulationOrderResDto> closeOrder(@RequestBody @Validated CloseSimulationOrderReqDto dto) {
            return ResponseEntity.ok(orderManagerService.close(dto));
    }
}
