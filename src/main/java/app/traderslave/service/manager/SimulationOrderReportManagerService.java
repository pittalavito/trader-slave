package app.traderslave.service.manager;

import app.traderslave.adapter.CandlesReqDtoAdapter;
import app.traderslave.assembler.CandlesResDtoAssembler;
import app.traderslave.checker.TimeChecker;
import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.controller.dto.TimeReqDto;
import app.traderslave.factory.OrderReportFactory;
import app.traderslave.model.domain.CandleBackTest;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.model.report.OrderReport;
import app.traderslave.service.domain.CandleBackTestDomainService;
import app.traderslave.service.BinanceService;
import app.traderslave.utility.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationOrderReportManagerService {

    private final BinanceService binanceService;
    private final CandleBackTestDomainService candleBackTestDomainService;

    public OrderReport createBackTestShortTermReport(Simulation simulation, SimulationOrder order, TimeReqDto dto) {
        LocalDateTime endTime = determineEndTime(dto);
        validateDates(order.getOpenTime(), endTime);
        CandlesReqDto candlesRequest = createCandlesRequest(order.getOpenTime(), simulation.getCurrencyPair(), endTime, TimeFrame.ONE_MINUTE);
        List<CandleBackTest> candlesBackTest = candleBackTestDomainService.getCandles(candlesRequest);
        CandlesResDto candlesResDto = CandlesResDtoAssembler.toModelBackTest(candlesBackTest);
        return OrderReportFactory.create(order, candlesResDto);
    }

    public Mono<OrderReport> createByBinanceApi(Simulation simulation, SimulationOrder order, TimeReqDto dto) {
        LocalDateTime endTime = determineEndTime(dto);
        validateDates(order.getOpenTime(), endTime);
        return isShortTerm(order.getOpenTime(), endTime) ?
                createShortTermReport(order, simulation.getCurrencyPair(), endTime) :
                createLongTermReport(order, simulation.getCurrencyPair(), endTime);
    }

    private Mono<OrderReport> createShortTermReport(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime endTime) {
        CandlesReqDto candlesReqDto = createCandlesRequest(order.getOpenTime(), currencyPair, endTime, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .map(candlesRes -> OrderReportFactory.create(order, candlesRes));
    }

    private Mono<OrderReport> createLongTermReport(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime endTime) {
        LocalDateTime midnightAfterOpening = getMidnightAfterOpening(order.getOpenTime());
        CandlesReqDto candlesReqDto = createCandlesRequest(order.getOpenTime(), currencyPair, midnightAfterOpening, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .flatMap(candlesRes -> handleLongTermInitialReport(order, currencyPair, midnightAfterOpening, endTime, candlesRes));
    }

    private Mono<OrderReport> handleLongTermInitialReport(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime midnightAfterOpening, LocalDateTime endTime, CandlesResDto candlesRes) {
        OrderReport report = OrderReportFactory.create(order, candlesRes);
        if (report.isLiquidated()) {
            return Mono.just(report);
        }
        return createLongTermReportStepTwo(order, currencyPair, midnightAfterOpening, endTime, report);
    }

    private Mono<OrderReport> createLongTermReportStepTwo(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime midnightAfterOpening, LocalDateTime endTime, OrderReport rep1) {
        LocalDateTime midnightBeforeEndTime = getMidnightBeforeEndTime(endTime);
        CandlesReqDto candlesReqDto = createCandlesRequest(midnightAfterOpening, currencyPair, midnightBeforeEndTime, TimeFrame.ONE_DAY);
        return binanceService.findCandles(candlesReqDto)
                .flatMap(candlesRes -> handleLongTermStepTwo(order, currencyPair, endTime, rep1, candlesRes));
    }

    private Mono<OrderReport> handleLongTermStepTwo(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime endTime, OrderReport rep1, CandlesResDto candlesRes) {
        OrderReport repTimeFrameOneDay = OrderReportFactory.create(order, candlesRes, rep1);
        if (repTimeFrameOneDay.isLiquidated()) {
            return handleLiquidation(order, currencyPair, endTime, repTimeFrameOneDay);
        }
        return createLongTermReportStepThree(order, currencyPair, getMidnightBeforeEndTime(endTime), endTime, repTimeFrameOneDay);
    }

    private Mono<OrderReport> handleLiquidation(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime endTime, OrderReport repTimeFrameOneDay) {
        LocalDateTime liquidationDay = getLiquidationDay(repTimeFrameOneDay);
        LocalDateTime closeTime = determineCloseTime(liquidationDay, endTime);
        CandlesReqDto candlesReqDto = createCandlesRequest(liquidationDay, currencyPair, closeTime, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .map(candlesRes2 -> OrderReportFactory.create(order, candlesRes2, repTimeFrameOneDay));
    }

    private Mono<OrderReport> createLongTermReportStepThree(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime midnightBeforeEndTime, LocalDateTime endTime, OrderReport rep1) {
        CandlesReqDto candlesReqDto = createCandlesRequest(midnightBeforeEndTime, currencyPair, endTime, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .map(candlesRes -> OrderReportFactory.create(order, candlesRes, rep1));
    }

    private void validateDates(LocalDateTime openTime, LocalDateTime endTime) {
        TimeChecker.checkDates(openTime, endTime);
    }

    private LocalDateTime determineEndTime(TimeReqDto dto) {
        return dto.isRealTimeRequest() ? TimeUtils.now() : dto.getStartTime();
    }

    private boolean isShortTerm(LocalDateTime openTime, LocalDateTime endTime) {
        return Duration.between(openTime, endTime).toHours() <= 180;
    }

    private LocalDateTime getMidnightAfterOpening(LocalDateTime openTime) {
        return openTime.plusDays(1).toLocalDate().atStartOfDay();
    }

    private LocalDateTime getMidnightBeforeEndTime(LocalDateTime endTime) {
        return endTime.minusDays(1).toLocalDate().atStartOfDay();
    }

    private LocalDateTime getLiquidationDay(OrderReport report) {
        return report.getCloseTime().toLocalDate().atStartOfDay();
    }

    private LocalDateTime determineCloseTime(LocalDateTime liquidationDay, LocalDateTime endTime) {
        Duration duration = Duration.between(liquidationDay, endTime);
        return duration.toHours() < 25 ? endTime : liquidationDay.toLocalDate().atTime(23, 59, 59, 999);
    }

    private CandlesReqDto createCandlesRequest(LocalDateTime startTime, CurrencyPair currencyPair, LocalDateTime endTime, TimeFrame timeFrame) {
        return CandlesReqDtoAdapter.adapt(startTime, currencyPair, endTime, timeFrame);
    }
}
