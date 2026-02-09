package app.traderslave.service;

import app.traderslave.adapter.CandleAdapter;
import app.traderslave.assembler.CandleAssembler;
import app.traderslave.assembler.OrderReportAssembler;
import app.traderslave.checker.TimeChecker;
import app.traderslave.domain.model.BackTestBinanceCandle;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.service.BackTestCandleDomainService;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.dto.OrderReportDto;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.model.dto.req.TimeReqDto;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestOrderReportService {

    private final BackTestCandleDomainService backTestCandleDomainService;
    private final CandleAdapter candleAdapter;
    private final CandleAssembler candleAssembler;
    private final OrderReportAssembler orderReportAssembler;

    public OrderReportDto createShortTermReport(BackTestPortfolio backTestPortfolio, BackTestOrder order, TimeReqDto dto) {
        LocalDateTime endTime = determineEndTime(dto);
        validateDates(order.getOpenTime(), endTime);
        CandlesReqDto candlesRequest = candleAdapter.adapt(order.getOpenTime(), backTestPortfolio.getCurrencyPair(), endTime, TimeFrame.FIVE_MINUTES);
        List<BackTestBinanceCandle> candlesBackTest = backTestCandleDomainService.getCandles(candlesRequest);
        List<CandleDto> candlesResDto = candleAssembler.toModel(candlesBackTest);
        return orderReportAssembler.toModel(order, candlesResDto);
    }

    private LocalDateTime determineEndTime(TimeReqDto dto) {
        return dto.isRealTimeRequest() ? TimeUtils.now() : dto.getStartTime();
    }

    private void validateDates(LocalDateTime openTime, LocalDateTime endTime) {
        TimeChecker.checkDates(openTime, endTime);
    }

}
