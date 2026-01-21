package app.traderslave.service;

import app.traderslave.model.domain.BinanceCandle;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import app.traderslave.repository.BinanceCandleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BinanceCandleService {

    private final BinanceCandleRepository binanceCandleRepository;

    public Optional<BinanceCandle> findByUid(BinanceGetKlinesRequestDto requestDto) {
        return binanceCandleRepository.findByUid(generateUid(requestDto));
    }

    public void add(BinanceGetKlinesRequestDto requestDto, List<Object[]> candles) {
        BinanceCandle binanceCandle = new BinanceCandle();
        binanceCandle.setUid(generateUid(requestDto));
        binanceCandle.setCandles(candles);
        binanceCandleRepository.save(binanceCandle);
    }

    private String generateUid(BinanceGetKlinesRequestDto requestDto) {
        return requestDto.getSymbol()
                .concat("_")
                .concat(requestDto.getInterval())
                .concat("_")
                .concat(String.valueOf(requestDto.getStartTime()))
                .concat("_")
                .concat(String.valueOf(requestDto.getEndTime()))
                .concat("_")
                .concat(String.valueOf(requestDto.getLimit()));
    }
}
