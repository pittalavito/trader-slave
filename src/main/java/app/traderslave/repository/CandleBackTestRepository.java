package app.traderslave.repository;

import app.traderslave.model.domain.CandleBackTest;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandleBackTestRepository extends JpaRepository<CandleBackTest, Long> {

    Optional<CandleBackTest> findByCurrencyPairAndTimeFrameAndOpenTimeAndCloseTime(CurrencyPair currencyPair, TimeFrame timeFrame, LocalDateTime openTime, LocalDateTime closeTime);

    Optional<CandleBackTest> findByUid(String uid);

    List<CandleBackTest> findAllByCurrencyPairAndTimeFrameAndOpenTimeGreaterThanEqualAndCloseTimeLessThanEqualOrderByOpenTimeAsc(CurrencyPair currencyPair, TimeFrame timeFrame, LocalDateTime startTime, LocalDateTime endTime);
}
