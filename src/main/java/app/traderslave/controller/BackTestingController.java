package app.traderslave.controller;

import app.traderslave.controller.dto.PostSimulationReqDto;
import app.traderslave.controller.dto.PostSimulationResDto;
import app.traderslave.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/back-testing")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestingController {

    private static final String URI_SIMULATION = "/simulation";

    private final SimulationService simulationService;

    /**
     * Create a simulation
     * Request:
     *  - currencyPair
     *  - startDate
     *  - endDate
     *  - timeFrame
     * Response:
     *  - balance
     *  - simulationId
     *  - currency
     *  - timeFrame
     *  - candles
     */
    @PostMapping(path = URI_SIMULATION)
    public Mono<ResponseEntity<PostSimulationResDto>> postSimulation(@RequestBody @Validated PostSimulationReqDto reqDto) {
        return simulationService.create(reqDto)
                .map(ResponseEntity::ok);
    }

    /**
     * Get simulation status
     * Request:
     *  - simulation-id
     * Response:
     *  - balance
     *  - pending-order
     *  - open-order
     */
    @GetMapping(path = URI_SIMULATION)
    public void getSimulation(@RequestParam Long simulationId) {
        //todo implement
    }

    /**
     * Close simulation
     * Request:
     *  - simulation-id
     * Response:
     *  - report
     */
    @DeleteMapping(path = URI_SIMULATION)
    public void deleteSimulation(@RequestParam Long simulationId) {
        //todo implement
    }

    /**
     * Put simulation
     * Request:
     *  - simulation-id
     *  - Order
     * Response:
     *  - balance
     *  - pending-order
     *  - open-order
     */
    @PutMapping(path = URI_SIMULATION)
    public void putSimulation(@RequestParam Long simulationId) {
        //todo al posto di questo considera una open order e close anche le operazioni spot etichettiamole come chiudi apri.
    }

}
