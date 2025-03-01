package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.exception.custom.BinanceRemoteException;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.custom.StartDateIsAfterNowException;
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
     * Creates a new simulation for the specified currency pair.
     *
     * @param currencyPair the currency pair for which the simulation is created
     * @return a Mono emitting a ResponseEntity containing the PostSimulationResDto with the simulation details
     * @throws IllegalArgumentException if the `currencyPair` is null or invalid
     */
    @PostMapping(path = URI_SIMULATION)
    public Mono<ResponseEntity<PostSimulationResDto>> createSimulation(@RequestParam CurrencyPair currencyPair) {
        return service.createSimulation(currencyPair)
                .map(ResponseEntity::ok);
    }

    /**
     * Creates a new order for the specified simulation.
     *
     * @param reqDto the request DTO containing the details of the order to be created
     *               - `simulationId`: The ID of the simulation for which the order is created. This field is mandatory.
     *               - `orderType`: The type of the order (e.g., BUY, SELL). This field is mandatory.
     *               - `amountOfTrade`: The amount of trade for the order. This field must have a minimum size of 10.
     *               - `time`: The time at which the order is created. Defaults to the current time if not provided.
     *               - `leverage`: The leverage for the order. This field must be between 1 and 100.
     * @return a Mono emitting a ResponseEntity containing the SimulationOrderResDto with the order details
     * @throws IllegalArgumentException if the `simulationId or OrderType` is null or invalid
     * @throws StartDateIsAfterNowException if time date is after now
     * @throws CustomException if simulation not found
     * @throws CustomException if balance is insufficient
     * @throws BinanceRemoteException if remote service throw error
     */
    @PostMapping(path = URI_ORDER)
    public Mono<ResponseEntity<SimulationOrderResDto>> createOrder(@RequestBody CreateSimulationOrderReqDto reqDto) {
        return service.createOrder(reqDto)
                .map(ResponseEntity::ok);
    }

    /**
     * Closes an existing order for the specified simulation.
     *
     * @param id the ID of the order to be closed
     * @param reqDto the request DTO containing the details of the order to be closed
     *               - `simulationId`: The ID of the simulation for which the order is closed. This field is mandatory.
     *               - `orderType`: The type of the order (e.g., BUY, SELL). This field is mandatory.
     *               - `amountOfTrade`: The amount of trade for the order. This field must have a minimum size of 10. If not provided, it defaults total available balance.
     *               - `time`: The time at which the order is closed. Defaults to the current time if not provided.
     *               - `leverage`: The leverage for the order. This field must be between 1 and 100. Defaults 1
     * @return a Mono emitting a ResponseEntity containing the CloseSimulationOrderResDto with the closed order details
     * @throws IllegalArgumentException if the `simulationId or OrderType` is null or invalid
     * @throws StartDateIsAfterNowException if time date is after now
     * @throws CustomException if simulation not found
     * @throws CustomException if simulation order not found
     * @throws CustomException if simulation order status is not open
     * @throws BinanceRemoteException if remote service throw error
     */
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
