package app.traderslave.domain.service;

import app.traderslave.model.dto.req.CandleReqDto;
import app.traderslave.model.dto.req.CandlesReqDto;
import app.traderslave.domain.model.BackTestBinanceCandle;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.domain.repository.BackTestCandleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestCandleDomainService {

    public static final int BATCH_SIZE = 500;

    private final BackTestCandleRepository backTestCandleRepository;

    public BackTestBinanceCandle getCandle(CandleReqDto candleReqDto) {
        var backTest = backTestCandleRepository.findAllByCurrencyPairAndTimeFrameAndOpenTimeGreaterThanEqualAndCloseTimeLessThanEqualOrderByOpenTimeAsc(
                candleReqDto.getCurrencyPair(),
                TimeFrame.ONE_MINUTE,
                candleReqDto.getStartTime(),
                candleReqDto.getStartTime().plusMinutes(1)
        );

        return backTest.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Candle not found"));
    }

    public List<BackTestBinanceCandle> getCandles(CandlesReqDto candlesReqDto) {
        return backTestCandleRepository.findAllByCurrencyPairAndTimeFrameAndOpenTimeGreaterThanEqualAndCloseTimeLessThanEqualOrderByOpenTimeAsc(
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
            backTestCandleRepository.saveAll(batch);
        }
        log.info("Total new candle back tests saved: {}", updated.size());
    }

    private BackTestBinanceCandle build(CandlesReqDto dto, CandleDto candle) {
        return BackTestBinanceCandle.builder()
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
