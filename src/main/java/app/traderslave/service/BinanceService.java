package app.traderslave.service;

import app.traderslave.adapter.BinanceServiceAdapter;
import app.traderslave.assembler.BinanceServiceAssembler;
import app.traderslave.checker.BinanceServiceChecker;
import app.traderslave.checker.TimeChecker;
import app.traderslave.controller.dto.*;
import app.traderslave.adapter.BinanceApiRequestAdapter;
import app.traderslave.adapter.BinanceApiResponseAdapter;
import app.traderslave.factory.ReportOrderFactory;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.domain.SimulationOrder;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.model.report.ReportOrder;
import app.traderslave.remote.api.BinanceApi;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import app.traderslave.utility.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BinanceService {

    private final BinanceApi binanceApi;

    public Mono<CandleResDto> findCandle(CandleReqDto dto) {
        BinanceServiceChecker.checkDatesGetKline(dto);

        return binanceApi.getKlines(BinanceApiRequestAdapter.adapt(dto))
                .map(BinanceApiResponseAdapter::adapt)
                .filter(CollectionUtils::hasUniqueObject)
                .map(CollectionUtils::firstElement);
    }

    public Mono<CandlesResDto> findCandles(CandlesReqDto dto) {
        BinanceServiceChecker.checkDatesGetKline(dto);

        return fetchCandleSticks(new HashSet<>(), BinanceApiRequestAdapter.adapt(dto))
                .collectList()
                .flatMap(BinanceServiceAssembler::toModel);
    }

    public Mono<ReportOrder> createReportOrder(Simulation simulation, SimulationOrder order, TimeReqDto dto) {
        LocalDateTime endTime = dto.isRealTimeRequest() ? TimeUtils.now() : dto.getStartTime();
        TimeChecker.checkDateOrder(order.getOpenTime(), endTime);
        Duration duration = Duration.between(order.getOpenTime(), endTime);

        if(duration.toHours() <=  180) {
            return findCandles(BinanceServiceAdapter.adapt(order.getOpenTime(), simulation, endTime, TimeFrame.ONE_MINUTE))
                    .map(candlesRes -> ReportOrderFactory.create(order, candlesRes));
        }

        return createReportForCaseMore180DaysStepOne(simulation, order, endTime);
    }

    private Mono<ReportOrder> createReportForCaseMore180DaysStepOne(Simulation simulation, SimulationOrder order, LocalDateTime endTime) {
        LocalDateTime midnightAfterOpening = order.getOpenTime().plusDays(1).toLocalDate().atStartOfDay();

        return findCandles(BinanceServiceAdapter.adapt(order.getOpenTime(), simulation, midnightAfterOpening, TimeFrame.ONE_MINUTE))
                .flatMap(candlesRes -> {
                    ReportOrder report = ReportOrderFactory.create(order, candlesRes);
                    return report.isLiquidated() ?
                            Mono.just(report) :
                            createReportForCaseMore180DaysStepTwo(simulation, order, midnightAfterOpening, endTime, report);
                });
    }

    private Mono<ReportOrder> createReportForCaseMore180DaysStepTwo(Simulation simulation, SimulationOrder order, LocalDateTime midnightAfterOpening, LocalDateTime endTime, ReportOrder rep1) {
        LocalDateTime midnightBeforeEndTime = endTime.minusDays(1).toLocalDate().atStartOfDay();

        return findCandles(BinanceServiceAdapter.adapt(midnightAfterOpening, simulation, midnightBeforeEndTime, TimeFrame.ONE_DAY))
                .flatMap(candlesRes -> {
                    ReportOrder repTimeFrameOneDay =  ReportOrderFactory.create(order, candlesRes, rep1);
                    if (repTimeFrameOneDay.isLiquidated()) {
                        LocalDateTime liquidationDay = repTimeFrameOneDay.getCloseTime().toLocalDate().atStartOfDay();
                        Duration duration = Duration.between(liquidationDay, endTime);
                        LocalDateTime closeTime = duration.toHours() < 25 ? endTime : liquidationDay.toLocalDate().atTime(23, 59, 59, 999);
                        return findCandles(BinanceServiceAdapter.adapt(liquidationDay, simulation, closeTime, TimeFrame.ONE_MINUTE))
                                .map(candlesRes2 -> ReportOrderFactory.create(order, candlesRes2, repTimeFrameOneDay));
                    }

                    ReportOrder rep2 = ReportOrderFactory.create(order, candlesRes, repTimeFrameOneDay);
                    return createReportForCaseMore180DaysStepThree(simulation, order, midnightBeforeEndTime, endTime, rep2);
                });
    }

    private Mono<ReportOrder> createReportForCaseMore180DaysStepThree(Simulation simulation, SimulationOrder order, LocalDateTime midnightBeforeEndTime, LocalDateTime endTime, ReportOrder rep1) {
        return findCandles(BinanceServiceAdapter.adapt(midnightBeforeEndTime, simulation, endTime, TimeFrame.ONE_MINUTE))
                .map(candlesRes -> ReportOrderFactory.create(order, candlesRes, rep1));
    }

    private Flux<CandleResDto> fetchCandleSticks(Set<CandleResDto> accumulatedCandlesList, BinanceGetKlinesRequestDto clientReqDto) {
        return binanceApi.getKlines(clientReqDto)
                .flatMapMany(clientRes -> {
                    if (!CollectionUtils.isEmpty(clientRes)) {
                        accumulatedCandlesList.addAll(BinanceApiResponseAdapter.adapt(clientRes));
                        // close time last candle response in millisecond + 1
                        clientReqDto.setStartTime(((Long) clientRes.get(clientRes.size() - 1)[6]) + 1);
                        return fetchCandleSticks(accumulatedCandlesList, clientReqDto);
                    }
                    return Flux.fromIterable(accumulatedCandlesList);
                });
    }
}


