package app.traderslave.domain.repository;

import app.traderslave.domain.model.BackTestPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackTestPortfolioRepository extends JpaRepository<BackTestPortfolio, Long> {
}
