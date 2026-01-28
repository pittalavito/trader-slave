package app.traderslave.service;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.model.domain.BinanceCandleBackTest;
import app.traderslave.repository.BinanceCandleBsckTestRepository;
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
public class BinanceCandleBackTestService {

    public static final int BATCH_SIZE = 250;

    private final BinanceCandleBsckTestRepository binanceCandleBsckTestRepository;

    public BinanceCandleBackTest getCandle() {
        return null;
    }

    public List<BinanceCandleBackTest> getCandles(CandlesReqDto candlesReqDto) {
        return binanceCandleBsckTestRepository.findAllByCurrencyPairAndTimeFrameAndOpenTimeGreaterThanEqualAndCloseTimeLessThanEqualOrderByOpenTimeAsc(
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
                .map(candleRto -> buildIfAbsent(candleRto, candleDto))
                .filter(Optional::isPresent)
                .toList();

        for (int i = 0; i < updated.size(); i += BATCH_SIZE) {
            var batch = updated.subList(i, Math.min(i + BATCH_SIZE, updated.size()))
                    .stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            binanceCandleBsckTestRepository.saveAll(batch);
            log.info("Saved batch of {} candle back tests", batch.size());
        }
        log.info("Total new candle back tests saved: {}", updated.size());
    }

    private Optional<BinanceCandleBackTest> buildIfAbsent(CandleResDto candleRto, CandlesReqDto candleDto) {
        var existingCandle = binanceCandleBsckTestRepository.findByUid(generateUid(candleDto, candleRto));
        if (existingCandle.isEmpty()) {
            log.info("Saving candle back test: {}", candleRto);
            return Optional.of(build(candleRto, candleDto));
        }
        log.info("Candle back test already exists: {}", candleRto);
        return Optional.empty();
    }

    private BinanceCandleBackTest build(CandleResDto candleRto, CandlesReqDto candleDto) {
        return BinanceCandleBackTest.builder()
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
