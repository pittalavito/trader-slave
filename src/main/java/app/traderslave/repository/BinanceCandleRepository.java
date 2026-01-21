package app.traderslave.repository;

import app.traderslave.model.domain.BinanceCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BinanceCandleRepository extends JpaRepository<BinanceCandle, Long> {
    Optional<BinanceCandle> findByUid(String uid);
}
