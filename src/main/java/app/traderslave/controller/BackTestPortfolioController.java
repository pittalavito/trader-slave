package app.traderslave.controller;

import app.traderslave.command.BackTestPatternStrategyCommand;
import app.traderslave.model.dto.req.BackTestCloseOrderReqDto;
import app.traderslave.model.dto.req.BackTestClosePortfolioReqDto;
import app.traderslave.model.dto.req.BackTestCreateOrderReqDto;
import app.traderslave.model.dto.req.BackTestCreatePortfolioReqDto;
import app.traderslave.model.dto.CloseBackTestPortfolioDto;
import app.traderslave.model.dto.BackTestCreatePortfolioDto;
import app.traderslave.model.dto.BackTestOrderDto;
import app.traderslave.service.BackTestPortfolioService;
import app.traderslave.service.BackTestOrderService;
import app.traderslave.utils.ControllerPath;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(ControllerPath.BACK_TEST_PORTFOLIO)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestPortfolioController {

    private static final String URI_ORDER = "/order";
    private static final String URI_ALL = "/all";

    private final BackTestPortfolioService backTestPortfolioService;
    private final BackTestOrderService orderManagerService;
    private final BackTestPatternStrategyCommand backTestPatternStrategyCommand;

    @PostMapping
    public ResponseEntity<BackTestCreatePortfolioDto> create(@RequestBody @Validated BackTestCreatePortfolioReqDto dto) {
        return ResponseEntity.ok(backTestPortfolioService.create(dto));
    }

    @PutMapping
    public ResponseEntity<CloseBackTestPortfolioDto> close(@RequestBody @Validated BackTestClosePortfolioReqDto dto) {
        return ResponseEntity.ok(backTestPortfolioService.close(dto));
    }

    @PostMapping(path = URI_ORDER)
    public ResponseEntity<BackTestOrderDto> createOrder(@RequestBody @Validated BackTestCreateOrderReqDto dto) {
        return ResponseEntity.ok(orderManagerService.create(dto));
    }

    @PutMapping(path = URI_ORDER)
    public ResponseEntity<BackTestOrderDto> closeOrder(@RequestBody @Validated BackTestCloseOrderReqDto dto) {
        return ResponseEntity.ok(orderManagerService.close(dto));
    }

    @Transactional
    @DeleteMapping(path = URI_ALL)
    public void deleteAll() {
        backTestPortfolioService.deleteAll();
    }
}
