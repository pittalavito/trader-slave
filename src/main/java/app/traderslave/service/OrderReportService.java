package app.traderslave.service;

import app.traderslave.adapter.CandleAdapter;
import app.traderslave.assembler.CandleAssembler;
import app.traderslave.checker.TimeChecker;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.model.dto.req.TimeReqDto;
import app.traderslave.assembler.OrderReportAssembler;
import app.traderslave.domain.model.BackTestBinanceCandle;
import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.model.dto.OrderReportDto;
import app.traderslave.domain.service.BackTestCandleDomainService;
import app.traderslave.remote.service.BinanceRemoteService;
import app.traderslave.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

//todo to be refactored
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderReportService {

    private final BinanceRemoteService binanceRemoteService;
    private final BackTestCandleDomainService backTestCandleDomainService;
    private final CandleAssembler candleAssembler;
    private final OrderReportAssembler orderReportAssembler;

    private final CandleAdapter candleAdapter;

    public OrderReportDto createBackTestShortTermReport(BackTestPortfolio backTestPortfolio, BackTestOrder order, TimeReqDto dto) {
        LocalDateTime endTime = determineEndTime(dto);
        validateDates(order.getOpenTime(), endTime);
        CandlesReqDto candlesRequest = candleAdapter.adapt(order.getOpenTime(), backTestPortfolio.getCurrencyPair(), endTime, TimeFrame.FIVE_MINUTES);
        List<BackTestBinanceCandle> candlesBackTest = backTestCandleDomainService.getCandles(candlesRequest);
        List<CandleDto> candlesResDto = candleAssembler.toModel(candlesBackTest);
        return orderReportAssembler.toModel(order, candlesResDto);
    }

    public OrderReportDto createSync(BackTestPortfolio backTestPortfolio, BackTestOrder order, TimeReqDto dto) {
        return createAsync(backTestPortfolio, order, dto).block();
    }

    public Mono<OrderReportDto> createAsync(BackTestPortfolio backTestPortfolio, BackTestOrder order, TimeReqDto dto) {
        LocalDateTime endTime = determineEndTime(dto);
        validateDates(order.getOpenTime(), endTime);
        return isShortTerm(order.getOpenTime(), endTime) ?
                createShortTermReport(order, backTestPortfolio.getCurrencyPair(), endTime) :
                createLongTermReport(order, backTestPortfolio.getCurrencyPair(), endTime);
    }

    private Mono<OrderReportDto> createShortTermReport(BackTestOrder order, CurrencyPair currencyPair, LocalDateTime endTime) {
        CandlesReqDto candlesReqDto = candleAdapter.adapt(order.getOpenTime(), currencyPair, endTime, TimeFrame.ONE_MINUTE);
        return binanceRemoteService.findCandlesAsync(candlesReqDto)
                .map(candlesRes -> orderReportAssembler.toModel(order, candlesRes));
    }

    private Mono<OrderReportDto> createLongTermReport(BackTestOrder order, CurrencyPair currencyPair, LocalDateTime endTime) {
        LocalDateTime midnightAfterOpening = getMidnightAfterOpening(order.getOpenTime());
        CandlesReqDto candlesReqDto = candleAdapter.adapt(order.getOpenTime(), currencyPair, midnightAfterOpening, TimeFrame.ONE_MINUTE);
        return binanceRemoteService.findCandlesAsync(candlesReqDto)
                .flatMap(candlesRes -> handleLongTermInitialReport(order, currencyPair, midnightAfterOpening, endTime, candlesRes));
    }

    private Mono<OrderReportDto> handleLongTermInitialReport(BackTestOrder order, CurrencyPair currencyPair, LocalDateTime midnightAfterOpening, LocalDateTime endTime, List<CandleDto> candlesRes) {
        OrderReportDto report = orderReportAssembler.toModel(order, candlesRes);
        if (report.isLiquidated()) {
            return Mono.just(report);
        }
        return createLongTermReportStepTwo(order, currencyPair, midnightAfterOpening, endTime, report);
    }

    private Mono<OrderReportDto> createLongTermReportStepTwo(BackTestOrder order, CurrencyPair currencyPair, LocalDateTime midnightAfterOpening, LocalDateTime endTime, OrderReportDto rep1) {
        LocalDateTime midnightBeforeEndTime = getMidnightBeforeEndTime(endTime);
        CandlesReqDto candlesReqDto = candleAdapter.adapt(midnightAfterOpening, currencyPair, midnightBeforeEndTime, TimeFrame.ONE_DAY);
        return binanceRemoteService.findCandlesAsync(candlesReqDto)
                .flatMap(candlesRes -> handleLongTermStepTwo(order, currencyPair, endTime, rep1, candlesRes));
    }

    private Mono<OrderReportDto> handleLongTermStepTwo(BackTestOrder order, CurrencyPair currencyPair, LocalDateTime endTime, OrderReportDto rep1, List<CandleDto> candlesRes) {
        OrderReportDto repTimeFrameOneDay = orderReportAssembler.toModel(order, candlesRes, rep1);
        if (repTimeFrameOneDay.isLiquidated()) {
            return handleLiquidation(order, currencyPair, endTime, repTimeFrameOneDay);
        }
        return createLongTermReportStepThree(order, currencyPair, getMidnightBeforeEndTime(endTime), endTime, repTimeFrameOneDay);
    }

    private Mono<OrderReportDto> handleLiquidation(BackTestOrder order, CurrencyPair currencyPair, LocalDateTime endTime, OrderReportDto repTimeFrameOneDay) {
        LocalDateTime liquidationDay = getLiquidationDay(repTimeFrameOneDay);
        LocalDateTime closeTime = determineCloseTime(liquidationDay, endTime);
        CandlesReqDto candlesReqDto = candleAdapter.adapt(liquidationDay, currencyPair, closeTime, TimeFrame.ONE_MINUTE);
        return binanceRemoteService.findCandlesAsync(candlesReqDto)
                .map(candlesRes2 -> orderReportAssembler.toModel(order, candlesRes2, repTimeFrameOneDay));
    }

    private Mono<OrderReportDto> createLongTermReportStepThree(BackTestOrder order, CurrencyPair currencyPair, LocalDateTime midnightBeforeEndTime, LocalDateTime endTime, OrderReportDto rep1) {
        CandlesReqDto candlesReqDto = candleAdapter.adapt(midnightBeforeEndTime, currencyPair, endTime, TimeFrame.ONE_MINUTE);
        return binanceRemoteService.findCandlesAsync(candlesReqDto)
                .map(candlesRes -> orderReportAssembler.toModel(order, candlesRes, rep1));
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
}
