package app.traderslave.domain.service;

import app.traderslave.model.dto.req.CandleReqDto;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.domain.model.CandleBackTest;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.domain.repository.CandleBackTestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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
    public void saveCandleBackTest(CandlesReqDto dto, List<CandleDto> candles) {
        var updated = candles.stream()
                .map(candle -> build(dto, candle))
                .toList();

        for (int i = 0; i < updated.size(); i += BATCH_SIZE) {
            var batch = updated.subList(i, Math.min(i + BATCH_SIZE, updated.size()))
                    .stream()
                    .toList();
            candleBackTestRepository.saveAll(batch);
        }
        log.info("Total new candle back tests saved: {}", updated.size());
    }

    private CandleBackTest build(CandlesReqDto dto, CandleDto candle) {
        return CandleBackTest.builder()
                .uid(generateUid(dto, candle))
                .currencyPair(dto.getCurrencyPair())
                .open(candle.getOpen())
                .high(candle.getHigh())
                .low(candle.getLow())
                .close(candle.getClose())
                .volume(candle.getVolume())
                .quoteAssetVolume(candle.getQuoteAssetVolume())
                .takerBuyBaseAssetVolume(candle.getTakerBuyBaseAssetVolume())
                .takerBuyQuoteAssetVolume(candle.getTakerBuyQuoteAssetVolume())
                .numberOfTrades(candle.getNumberOfTrades())
                .openTime(candle.getOpenTime())
                .closeTime(candle.getCloseTime())
                .timeFrame(dto.getTimeFrame())
                .version(0)
                .build();
    }

    private String generateUid(CandlesReqDto candleDto, CandleDto candleRto) {
        return candleDto.getCurrencyPair().name()
                + "_" + candleDto.getTimeFrame().getCode()
                + "_" + candleRto.getOpenTime()
                + "_" + candleRto.getCloseTime();
    }
}
