package app.traderslave.service.simulation;

import app.traderslave.assembler.CandleDtoAssembler;
import app.traderslave.checker.TimeChecker;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.model.dto.req.TimeReqDto;
import app.traderslave.assembler.OrderReportDtoAssembler;
import app.traderslave.domain.model.CandleBackTest;
import app.traderslave.domain.model.Simulation;
import app.traderslave.domain.model.SimulationOrder;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.model.dto.OrderReportDto;
import app.traderslave.domain.service.CandleBackTestDomainService;
import app.traderslave.remote.service.BinanceRemoteService;
import app.traderslave.utils.TimeUtils;
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

    private final BinanceRemoteService binanceRemoteService;
    private final CandleBackTestDomainService candleBackTestDomainService;

    public OrderReportDto createBackTestShortTermReport(Simulation simulation, SimulationOrder order, TimeReqDto dto) {
        LocalDateTime endTime = determineEndTime(dto);
        validateDates(order.getOpenTime(), endTime);
        CandlesReqDto candlesRequest = createCandlesRequest(order.getOpenTime(), simulation.getCurrencyPair(), endTime, TimeFrame.ONE_MINUTE);
        List<CandleBackTest> candlesBackTest = candleBackTestDomainService.getCandles(candlesRequest);
        List<CandleDto> candlesResDto = CandleDtoAssembler.toModel(candlesBackTest);
        return OrderReportDtoAssembler.create(order, candlesResDto);
    }

    public Mono<OrderReportDto> create(Simulation simulation, SimulationOrder order, TimeReqDto dto) {
        LocalDateTime endTime = determineEndTime(dto);
        validateDates(order.getOpenTime(), endTime);
        return isShortTerm(order.getOpenTime(), endTime) ?
                createShortTermReport(order, simulation.getCurrencyPair(), endTime) :
                createLongTermReport(order, simulation.getCurrencyPair(), endTime);
    }

    private Mono<OrderReportDto> createShortTermReport(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime endTime) {
        CandlesReqDto candlesReqDto = createCandlesRequest(order.getOpenTime(), currencyPair, endTime, TimeFrame.ONE_MINUTE);
        return binanceRemoteService.findCandlesAsync(candlesReqDto)
                .map(candlesRes -> OrderReportDtoAssembler.create(order, candlesRes));
    }

    private Mono<OrderReportDto> createLongTermReport(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime endTime) {
        LocalDateTime midnightAfterOpening = getMidnightAfterOpening(order.getOpenTime());
        CandlesReqDto candlesReqDto = createCandlesRequest(order.getOpenTime(), currencyPair, midnightAfterOpening, TimeFrame.ONE_MINUTE);
        return binanceRemoteService.findCandlesAsync(candlesReqDto)
                .flatMap(candlesRes -> handleLongTermInitialReport(order, currencyPair, midnightAfterOpening, endTime, candlesRes));
    }

    private Mono<OrderReportDto> handleLongTermInitialReport(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime midnightAfterOpening, LocalDateTime endTime, List<CandleDto> candlesRes) {
        OrderReportDto report = OrderReportDtoAssembler.create(order, candlesRes);
        if (report.isLiquidated()) {
            return Mono.just(report);
        }
        return createLongTermReportStepTwo(order, currencyPair, midnightAfterOpening, endTime, report);
    }

    private Mono<OrderReportDto> createLongTermReportStepTwo(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime midnightAfterOpening, LocalDateTime endTime, OrderReportDto rep1) {
        LocalDateTime midnightBeforeEndTime = getMidnightBeforeEndTime(endTime);
        CandlesReqDto candlesReqDto = createCandlesRequest(midnightAfterOpening, currencyPair, midnightBeforeEndTime, TimeFrame.ONE_DAY);
        return binanceRemoteService.findCandlesAsync(candlesReqDto)
                .flatMap(candlesRes -> handleLongTermStepTwo(order, currencyPair, endTime, rep1, candlesRes));
    }

    private Mono<OrderReportDto> handleLongTermStepTwo(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime endTime, OrderReportDto rep1, List<CandleDto> candlesRes) {
        OrderReportDto repTimeFrameOneDay = OrderReportDtoAssembler.create(order, candlesRes, rep1);
        if (repTimeFrameOneDay.isLiquidated()) {
            return handleLiquidation(order, currencyPair, endTime, repTimeFrameOneDay);
        }
        return createLongTermReportStepThree(order, currencyPair, getMidnightBeforeEndTime(endTime), endTime, repTimeFrameOneDay);
    }

    private Mono<OrderReportDto> handleLiquidation(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime endTime, OrderReportDto repTimeFrameOneDay) {
        LocalDateTime liquidationDay = getLiquidationDay(repTimeFrameOneDay);
        LocalDateTime closeTime = determineCloseTime(liquidationDay, endTime);
        CandlesReqDto candlesReqDto = createCandlesRequest(liquidationDay, currencyPair, closeTime, TimeFrame.ONE_MINUTE);
        return binanceRemoteService.findCandlesAsync(candlesReqDto)
                .map(candlesRes2 -> OrderReportDtoAssembler.create(order, candlesRes2, repTimeFrameOneDay));
    }

    private Mono<OrderReportDto> createLongTermReportStepThree(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime midnightBeforeEndTime, LocalDateTime endTime, OrderReportDto rep1) {
        CandlesReqDto candlesReqDto = createCandlesRequest(midnightBeforeEndTime, currencyPair, endTime, TimeFrame.ONE_MINUTE);
        return binanceRemoteService.findCandlesAsync(candlesReqDto)
                .map(candlesRes -> OrderReportDtoAssembler.create(order, candlesRes, rep1));
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

    private LocalDateTime getLiquidationDay(OrderReportDto report) {
        return report.getCloseTime().toLocalDate().atStartOfDay();
    }

    private LocalDateTime determineCloseTime(LocalDateTime liquidationDay, LocalDateTime endTime) {
        Duration duration = Duration.between(liquidationDay, endTime);
        return duration.toHours() < 25 ? endTime : liquidationDay.toLocalDate().atTime(23, 59, 59, 999);
    }

    private CandlesReqDto createCandlesRequest(LocalDateTime startTime, CurrencyPair currencyPair, LocalDateTime endTime, TimeFrame timeFrame) {
        CandlesReqDto reqDto = new CandlesReqDto();
        reqDto.setCurrencyPair(currencyPair);
        reqDto.setStartTime(startTime);
        reqDto.setEndTime(endTime);
        reqDto.setTimeFrame(timeFrame);
        return reqDto;
    }
}
