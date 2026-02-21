package app.traderslave.bot.domain.repository;

import app.traderslave.bot.domain.model.BackTestBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BackTestBotRepository extends JpaRepository<BackTestBot, Long> {

    Optional<BackTestBot> findByPortfolioId(Long portfolioId);
}
