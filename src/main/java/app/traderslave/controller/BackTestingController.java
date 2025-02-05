package app.traderslave.controller;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/back-testing")
public class BackTestingController extends BaseController {

    private static final String URI_SIMULATION = "/simulation";

    public BackTestingController(BeanFactory beanFactory) {
        super(beanFactory);
    }

    /**
     * Create a simulation
     * Request:
     *  - currency pair,
     *  - start date
     *  - end date
     * Response:
     *  - balance account
     *  - id simulation
     */
    @PostMapping(path = URI_SIMULATION)
    public void postSimulation() {
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
    }


}
