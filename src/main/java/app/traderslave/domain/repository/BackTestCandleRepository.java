package app.traderslave.domain.repository;

import app.traderslave.domain.model.BackTestBinanceCandle;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BackTestCandleRepository extends JpaRepository<BackTestBinanceCandle, Long> {

    Optional<BackTestBinanceCandle> findByCurrencyPairAndTimeFrameAndOpenTimeAndCloseTime(CurrencyPair currencyPair, TimeFrame timeFrame, LocalDateTime openTime, LocalDateTime closeTime);

    Optional<BackTestBinanceCandle> findByUid(String uid);

    List<BackTestBinanceCandle> findAllByCurrencyPairAndTimeFrameAndOpenTimeGreaterThanEqualAndCloseTimeLessThanEqualOrderByOpenTimeAsc(CurrencyPair currencyPair, TimeFrame timeFrame, LocalDateTime startTime, LocalDateTime endTime);
}
