package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/simulation")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationController {

    private static final String URI_ORDER = "/order";
    private static final String URI_ALL = "/all";

    private final SimulationService simulationService;

    // --- SIMULATION --------------------------------------------------------------------------------------------------

    @PostMapping
    public Mono<ResponseEntity<PostSimulationResDto>> create(@RequestBody @Validated CreateSimulationReqDto dto) {
        return simulationService.create(dto)
                .map(ResponseEntity::ok);
    }

    @PutMapping
    public Mono<ResponseEntity<CloseSimulationResDto>> close(@RequestBody @Validated CloseSimulationReqDto dto) {
        return simulationService.close(dto)
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
