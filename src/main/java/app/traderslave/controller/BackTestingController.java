package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.service.BackTestingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/back-testing")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestingController {

    private static final String URI_SIMULATION = "/simulation";
    private static final String URI_SIMULATION_ID = "/simulation/{id}";
    private static final String URI_ORDER = "/order";
    private static final String URI_ORDER_ID = "/order/{id}";

    private final BackTestingService service;

    @PostMapping(path = URI_SIMULATION)
    public Mono<ResponseEntity<PostSimulationResDto>> createSimulation(@RequestParam CurrencyPair currencyPair) {
        return service.createSimulation(currencyPair)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = URI_ORDER)
    public Mono<ResponseEntity<SimulationOrderResDto>> createOrder(@RequestBody CreateSimulationOrderReqDto reqDto) {
        return service.createOrder(reqDto)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = URI_ORDER_ID)
    public Mono<ResponseEntity<CloseSimulationOrderResDto>> closeOrder(@PathVariable Long id, @RequestBody CloseSimulationOrderReqDto reqDto) {
        return service.closeOrder(id, reqDto)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = URI_SIMULATION_ID)
    public Object closeSimulation(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean delete) {
        //todo implement
        return null;
    }

    //todo capire se implementare
    //@GetMapping(path = URI_SIMULATION_ID)
    //public Mono<ResponseEntity<GetSimulationResDto>> getSimulation(@PathVariable Long id, @RequestParam(required = false) LocalDateTime time) {
    //    time = time == null ? TimeUtils.now() : time;
    //    return service.getSimulationStatus(id, time)
    //            .map(ResponseEntity::ok);
    //    return null;
    //}
}
