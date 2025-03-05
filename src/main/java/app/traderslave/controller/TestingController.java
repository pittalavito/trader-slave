package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.service.TestingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/testing")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestingController {

    private static final String URI_SIMULATION = "/simulation";
    private static final String URI_ORDER = "/order";

    private final TestingService service;

    @PostMapping(path = URI_SIMULATION)
    public Mono<ResponseEntity<PostSimulationResDto>> createSimulation(@RequestBody @Validated CreateSimulationReqDto dto) {
        return service.createSimulation(dto)
                .map(ResponseEntity::ok);
    }

    @PutMapping(path = URI_SIMULATION)
    public Mono<ResponseEntity<CloseSimulationResDto>> closeSimulation(@RequestBody @Validated CloseSimulationReqDto dto) {
        return service.closeSimulation(dto)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = URI_ORDER)
    public Mono<ResponseEntity<SimulationOrderResDto>> createOrder(@RequestBody @Validated CreateSimulationOrderReqDto dto) {
        return service.createOrder(dto)
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = URI_ORDER)
    public Mono<ResponseEntity<SimulationOrderResDto>> getOrder(@ModelAttribute @Validated GetSimulationOrderReqDto dto) {
        return service.getOrder(dto)
                .map(ResponseEntity::ok);
    }

    @PutMapping(path = URI_ORDER)
    public Mono<ResponseEntity<SimulationOrderResDto>> closeOrder(@RequestBody @Validated CloseSimulationOrderReqDto dto) {
        return service.closeOrder(dto)
                .map(ResponseEntity::ok);
    }
}
