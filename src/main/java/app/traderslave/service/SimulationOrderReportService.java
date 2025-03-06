package app.traderslave.service;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.checker.TimeChecker;
import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.controller.dto.TimeReqDto;
import app.traderslave.exception.custom.CustomException;
import app.traderslave.exception.model.ExceptionEnum;
import app.traderslave.factory.OrderReportFactory;
import app.traderslave.model.domain.SimulationOrder;
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
class SimulationOrderReportService {

    private final BinanceService binanceService;

    public Mono<OrderReport> create(SimulationOrder order, TimeReqDto dto) {
        LocalDateTime endTime = dto.isRealTimeRequest() ? TimeUtils.now() : dto.getStartTime();
        TimeChecker.checkDateOrder(order.getOpenTime(), endTime);
        if (order.getSimulation() == null) {
            throw new CustomException(ExceptionEnum.TRANSIENT_SIMULATION_NOT_FOUND);
        }
        return Duration.between(order.getOpenTime(), endTime).toHours() <= 180 ?
                createShortTermReport(order, endTime) :
                createLongTermReport(order, endTime);
    }

    private Mono<OrderReport> createShortTermReport(SimulationOrder order, LocalDateTime endTime) {
        CandlesReqDto candlesReqDto = BinanceServiceAdapter.adapt(order.getOpenTime(), order.getSimulation().getCurrencyPair(), endTime, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .map(candlesRes -> OrderReportFactory.create(order, candlesRes));
    }

    private Mono<OrderReport> createLongTermReport(SimulationOrder order, LocalDateTime endTime) {
        LocalDateTime midnightAfterOpening = order.getOpenTime().plusDays(1).toLocalDate().atStartOfDay();
        CandlesReqDto candlesReqDto = BinanceServiceAdapter.adapt(order.getOpenTime(), order.getSimulation().getCurrencyPair(), midnightAfterOpening, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .flatMap(candlesRes -> {
                    OrderReport report = OrderReportFactory.create(order, candlesRes);
                    if (report.isLiquidated()) {
                        return Mono.just(report);
                    }
                    return createLongTermReportStepTwo(order, midnightAfterOpening, endTime, report);
                });
    }

    private Mono<OrderReport> createLongTermReportStepTwo(SimulationOrder order, LocalDateTime midnightAfterOpening, LocalDateTime endTime, OrderReport rep1) {
        LocalDateTime midnightBeforeEndTime = endTime.minusDays(1).toLocalDate().atStartOfDay();
        CandlesReqDto candlesReqDto = BinanceServiceAdapter.adapt(midnightAfterOpening, order.getSimulation().getCurrencyPair(), midnightBeforeEndTime, TimeFrame.ONE_DAY);
        return binanceService.findCandles(candlesReqDto)
                .flatMap(candlesRes -> {
                    OrderReport repTimeFrameOneDay = OrderReportFactory.create(order, candlesRes, rep1);
                    if (repTimeFrameOneDay.isLiquidated()) {
                        return handleLiquidationCreateLongTermReportStepTwo(order, endTime, repTimeFrameOneDay);
                    }
                    OrderReport rep2 = OrderReportFactory.create(order, candlesRes, repTimeFrameOneDay);
                    return createLongTermReportStepThree(order, midnightBeforeEndTime, endTime, rep2);
                });
    }

    private Mono<OrderReport> handleLiquidationCreateLongTermReportStepTwo(SimulationOrder order, LocalDateTime endTime, OrderReport repTimeFrameOneDay) {
        LocalDateTime liquidationDay = repTimeFrameOneDay.getCloseTime().toLocalDate().atStartOfDay();
        Duration duration = Duration.between(liquidationDay, endTime);
        LocalDateTime closeTime = duration.toHours() < 25 ? endTime : liquidationDay.toLocalDate().atTime(23, 59, 59, 999);
        CandlesReqDto candlesReqDto = BinanceServiceAdapter.adapt(liquidationDay, order.getSimulation().getCurrencyPair(), closeTime, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .map(candlesRes2 -> OrderReportFactory.create(order, candlesRes2, repTimeFrameOneDay));
    }

    private Mono<OrderReport> createLongTermReportStepThree(SimulationOrder order, LocalDateTime midnightBeforeEndTime, LocalDateTime endTime, OrderReport rep1) {
        CandlesReqDto candlesReqDto = BinanceServiceAdapter.adapt(midnightBeforeEndTime, order.getSimulation().getCurrencyPair(), endTime, TimeFrame.ONE_MINUTE);
        return binanceService.findCandles(candlesReqDto)
                .map(candlesRes -> OrderReportFactory.create(order, candlesRes, rep1));
    }
}
