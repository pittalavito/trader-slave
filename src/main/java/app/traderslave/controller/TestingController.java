package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.service.BackTestingService;
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
    private static final String URI_SIMULATION_ID = "/simulation/{id}";
    private static final String URI_ORDER = "/order";
    private static final String URI_ORDER_ID = "/order/{id}";

    private final BackTestingService service;

    @PostMapping(path = URI_SIMULATION)
    public Mono<ResponseEntity<PostSimulationResDto>> createSimulation(@RequestBody @Validated CreateSimulationReqDto dto) {
        return service.createSimulation(dto)
                .map(ResponseEntity::ok);
    }

    //@GetMapping(path = URI_SIMULATION_ID)
    //public Mono<ResponseEntity<GetSimulationResDto>> getSimulation(@PathVariable Long id, @RequestParam(required = false) LocalDateTime time) {
    //    return service.getSimulation(id, time)
    //            .map(ResponseEntity::ok);
    //
    //}
    //
    //@PostMapping(path = URI_SIMULATION_ID)
    //public Mono<ResponseEntity<GetSimulationResDto>> closeSimulation(@PathVariable Long id, @RequestParam(required = false) LocalDateTime time, @RequestParam(defaultValue = "true") boolean delete) {
    //    return service.closeSimulation(id, time, delete)
    //            .map(ResponseEntity::ok);
    //}

    @PostMapping(path = URI_ORDER)
    public Mono<ResponseEntity<SimulationOrderResDto>> createOrder(@RequestBody CreateSimulationOrderReqDto reqDto) {
        return service.createOrder(reqDto)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = URI_ORDER_ID)
    public Mono<ResponseEntity<SimulationOrderResDto>> closeOrder(@PathVariable Long id, @RequestBody CloseSimulationOrderReqDto reqDto) {
        return service.closeOrder(id, reqDto)
                .map(ResponseEntity::ok);
    }
}
