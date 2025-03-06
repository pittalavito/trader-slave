package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/simulation")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationController {

    private static final String URI_ORDER = "/order";
    private static final String URI_ALL = "/all";

    private final SimulationService simulationService;

    // --- SIMULATION --------------------------------------------------------------------------------------------------
    @GetMapping
    public Mono<ResponseEntity<GetSimulationResDto>> retrieve(@ModelAttribute @Validated GetSimulationReqDto dto) {
        return simulationService.retrieve(dto)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<PostSimulationResDto>> create(@RequestBody @Validated CreateSimulationReqDto dto) {
        return simulationService.create(dto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping
    public void delete(@RequestParam Long simulationId) {
        //todo
    }

    @GetMapping(path = URI_ALL)
    public void retrieveAll() {
        //todo
    }

    @DeleteMapping(path = URI_ALL)
    public void deleteAll() {
        //todo
    }

    // --- ORDER _____--------------------------------------------------------------------------------------------------

    @GetMapping(path = URI_ORDER)
    public Mono<ResponseEntity<SimulationOrderResDto>> retrieveOrder(@ModelAttribute @Validated GetSimulationOrderReqDto dto) {
        return simulationService.retrieveOrder(dto)
                .map(ResponseEntity::ok);
    }

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
