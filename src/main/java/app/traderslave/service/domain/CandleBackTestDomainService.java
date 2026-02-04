package app.traderslave.service.domain;

import app.traderslave.controller.dto.CandleReqDto;
import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.model.domain.CandleBackTest;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.repository.CandleBackTestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CandleBackTestDomainService {

    public static final int BATCH_SIZE = 500;

    private final CandleBackTestRepository candleBackTestRepository;

    public CandleBackTest getCandle(CandleReqDto candleReqDto) {
        var backTest = candleBackTestRepository.findAllByCurrencyPairAndTimeFrameAndOpenTimeGreaterThanEqualAndCloseTimeLessThanEqualOrderByOpenTimeAsc(
                candleReqDto.getCurrencyPair(),
                TimeFrame.ONE_MINUTE,
                candleReqDto.getStartTime(),
                candleReqDto.getStartTime().plusMinutes(1)
        );

        return backTest.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Candle not found"));
    }

    public List<CandleBackTest> getCandles(CandlesReqDto candlesReqDto) {
        return candleBackTestRepository.findAllByCurrencyPairAndTimeFrameAndOpenTimeGreaterThanEqualAndCloseTimeLessThanEqualOrderByOpenTimeAsc(
                candlesReqDto.getCurrencyPair(),
                candlesReqDto.getTimeFrame(),
                candlesReqDto.getStartTime(),
                candlesReqDto.getEndTime()
        );
    }

    @Transactional
    public void saveCandleBackTest(CandlesReqDto candleDto, CandlesResDto candlesRto) {
        saveCandleBackTest(candleDto, candlesRto.getList());
    }

    @Transactional
    public void saveCandleBackTest(CandlesReqDto candleDto, List<CandleResDto> candlesRtoList) {
        var updated = candlesRtoList.stream()
                .map(candleRto -> build(candleRto, candleDto))
                .toList();

        for (int i = 0; i < updated.size(); i += BATCH_SIZE) {
            var batch = updated.subList(i, Math.min(i + BATCH_SIZE, updated.size()))
                    .stream()
                    .toList();
            candleBackTestRepository.saveAll(batch);
        }
        log.info("Total new candle back tests saved: {}", updated.size());
    }

    private Optional<CandleBackTest> buildIfAbsent(CandleResDto candleRto, CandlesReqDto candleDto) {
        var existingCandle = candleBackTestRepository.findByUid(generateUid(candleDto, candleRto));
        if (existingCandle.isEmpty()) {
            return Optional.of(build(candleRto, candleDto));
        }
        return Optional.empty();
    }

    private CandleBackTest build(CandleResDto candleRto, CandlesReqDto candleDto) {
        return CandleBackTest.builder()
                .uid(generateUid(candleDto, candleRto))
                .currencyPair(candleDto.getCurrencyPair())
                .open(candleRto.getOpen())
                .high(candleRto.getHigh())
                .low(candleRto.getLow())
                .close(candleRto.getClose())
                .volume(candleRto.getVolume())
                .quoteAssetVolume(candleRto.getQuoteAssetVolume())
                .takerBuyBaseAssetVolume(candleRto.getTakerBuyBaseAssetVolume())
                .takerBuyQuoteAssetVolume(candleRto.getTakerBuyQuoteAssetVolume())
                .numberOfTrades(candleRto.getNumberOfTrades())
                .openTime(candleRto.getOpenTime())
                .closeTime(candleRto.getCloseTime())
                .timeFrame(candleDto.getTimeFrame())
                .version(0)
                .build();
    }

    private String generateUid(CandlesReqDto candleDto, CandleResDto candleRto) {
        return candleDto.getCurrencyPair().name()
                + "_" + candleDto.getTimeFrame().getCode()
                + "_" + candleRto.getOpenTime()
                + "_" + candleRto.getCloseTime();
    }
}
