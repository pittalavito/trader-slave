package app.traderslave.service;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.checker.TimeChecker;
import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.controller.dto.TimeReqDto;
import app.traderslave.factory.OrderReportFactory;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.model.report.OrderReport;
import app.traderslave.utility.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class SimulationOrderReportFactoryService {

    private final BinanceService binanceService;

    public Mono<OrderReport> create(Simulation simulation, SimulationOrder order, TimeReqDto dto) {
        LocalDateTime endTime = dto.isRealTimeRequest() ? TimeUtils.now() : dto.getStartTime();
        CurrencyPair currencyPair = simulation.getCurrencyPair();
        TimeChecker.checkDates(order.getOpenTime(), endTime);
        return Duration.between(order.getOpenTime(), endTime).toHours() <= 180 ?
                createShortTermReport(order, currencyPair, endTime) :
                createLongTermReport(order, currencyPair, endTime);
    }

    private Mono<OrderReport> createShortTermReport(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime endTime) {
        CandlesReqDto candlesReqDto = BinanceServiceAdapter.adapt(order.getOpenTime(), currencyPair, endTime, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .map(candlesRes -> OrderReportFactory.create(order, candlesRes));
    }

    private Mono<OrderReport> createLongTermReport(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime endTime) {
        LocalDateTime midnightAfterOpening = order.getOpenTime().plusDays(1).toLocalDate().atStartOfDay();
        CandlesReqDto candlesReqDto = BinanceServiceAdapter.adapt(order.getOpenTime(), currencyPair, midnightAfterOpening, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .flatMap(candlesRes -> {
                    OrderReport report = OrderReportFactory.create(order, candlesRes);
                    if (report.isLiquidated()) {
                        return Mono.just(report);
                    }
                    return createLongTermReportStepTwo(order, currencyPair, midnightAfterOpening, endTime, report);
                });
    }

    private Mono<OrderReport> createLongTermReportStepTwo(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime midnightAfterOpening, LocalDateTime endTime, OrderReport rep1) {
        LocalDateTime midnightBeforeEndTime = endTime.minusDays(1).toLocalDate().atStartOfDay();
        CandlesReqDto candlesReqDto = BinanceServiceAdapter.adapt(midnightAfterOpening, currencyPair, midnightBeforeEndTime, TimeFrame.ONE_DAY);
        return binanceService.findCandles(candlesReqDto)
                .flatMap(candlesRes -> {
                    OrderReport repTimeFrameOneDay = OrderReportFactory.create(order, candlesRes, rep1);
                    if (repTimeFrameOneDay.isLiquidated()) {
                        return handleLiquidationCreateLongTermReportStepTwo(order, currencyPair, endTime, repTimeFrameOneDay);
                    }
                    OrderReport rep2 = OrderReportFactory.create(order, candlesRes, repTimeFrameOneDay);
                    return createLongTermReportStepThree(order, currencyPair, midnightBeforeEndTime, endTime, rep2);
                });
    }

    private Mono<OrderReport> handleLiquidationCreateLongTermReportStepTwo(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime endTime, OrderReport repTimeFrameOneDay) {
        LocalDateTime liquidationDay = repTimeFrameOneDay.getCloseTime().toLocalDate().atStartOfDay();
        Duration duration = Duration.between(liquidationDay, endTime);
        LocalDateTime closeTime = duration.toHours() < 25 ? endTime : liquidationDay.toLocalDate().atTime(23, 59, 59, 999);
        CandlesReqDto candlesReqDto = BinanceServiceAdapter.adapt(liquidationDay, currencyPair, closeTime, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .map(candlesRes2 -> OrderReportFactory.create(order, candlesRes2, repTimeFrameOneDay));
    }

    private Mono<OrderReport> createLongTermReportStepThree(SimulationOrder order, CurrencyPair currencyPair, LocalDateTime midnightBeforeEndTime, LocalDateTime endTime, OrderReport rep1) {
        CandlesReqDto candlesReqDto = BinanceServiceAdapter.adapt(midnightBeforeEndTime, currencyPair, endTime, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .map(candlesRes -> OrderReportFactory.create(order, candlesRes, rep1));
    }
}
