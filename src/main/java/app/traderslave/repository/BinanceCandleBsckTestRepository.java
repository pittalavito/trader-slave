package app.traderslave.repository;

import app.traderslave.model.domain.BinanceCandleBackTest;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BinanceCandleBsckTestRepository extends JpaRepository<BinanceCandleBackTest, Long> {

    Optional<BinanceCandleBackTest> findByCurrencyPairAndTimeFrameAndOpenTimeAndCloseTime(CurrencyPair currencyPair, TimeFrame timeFrame, LocalDateTime openTime, LocalDateTime closeTime);

    Optional<BinanceCandleBackTest> findByUid(String uid);

    List<BinanceCandleBackTest> findAllByCurrencyPairAndTimeFrameAndOpenTimeGreaterThanEqualAndCloseTimeLessThanEqualOrderByOpenTimeAsc(CurrencyPair currencyPair, TimeFrame timeFrame, LocalDateTime startTime, LocalDateTime endTime);
}
