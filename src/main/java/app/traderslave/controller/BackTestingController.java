package app.traderslave.controller;

import app.traderslave.controller.dto.CreateSimulationOrderReqDto;
import app.traderslave.controller.dto.CreateSimulationOrderResDto;
import app.traderslave.controller.dto.PostSimulationResDto;
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

    /**
     * Create a simulation
     */
    @PostMapping(path = URI_SIMULATION)
    public Mono<ResponseEntity<PostSimulationResDto>> postSimulation(@RequestParam CurrencyPair currencyPair) {
        return service.createSimulation(currencyPair)
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = URI_SIMULATION_ID)
    public void getSimulation(@PathVariable Long id) {
        //todo implemen
    }

    @PostMapping(path = URI_SIMULATION_ID)
    public Object closeSimulation(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean delete) {
        //todo implement
        return null;
    }

    @PostMapping(path = URI_ORDER)
    public Mono<ResponseEntity<CreateSimulationOrderResDto>> createOrder(@RequestBody CreateSimulationOrderReqDto reqDto) {
        return service.createOrder(reqDto)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = URI_ORDER_ID)
    public void closeOrder(@PathVariable Long id, @RequestBody Object order) {
        //todo implement
    }

}
