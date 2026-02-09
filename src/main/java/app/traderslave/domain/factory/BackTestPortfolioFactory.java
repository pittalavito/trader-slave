package app.traderslave.domain.factory;

import app.traderslave.model.dto.req.BackTestCreatePortfolioReqDto;
import app.traderslave.model.dto.req.TimeReqDto;
import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.utils.TimeUtils;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class BackTestPortfolioFactory {

    public BackTestPortfolio create(BackTestCreatePortfolioReqDto dto) {
        return BackTestPortfolio.builder()
                .currencyPair(dto.getCurrencyPair())
                .currency(dto.getCurrency())
                .balance(dto.getCurrency().getDefaultCapital())
                .status(BackTestPortfolio.Status.OPEN)
                .startTime(dto.getStartTime())
                .uid(UUID.randomUUID().toString())
                .creationDate(LocalDateTime.now())
                .description(dto.getDescription())
                .dataSource(dto.getDataSource())
                .version(0)
                .build();
    }

    public BackTestPortfolio close(BackTestPortfolio backTestPortfolio, TimeReqDto dto) {
        backTestPortfolio.setStatus(BackTestPortfolio.Status.CLOSED);
        backTestPortfolio.setVersion(backTestPortfolio.getVersion() + 1);
        backTestPortfolio.setLastModificationDate(LocalDateTime.now());
        backTestPortfolio.setEndTime(dto.isRealTimeRequest() ? TimeUtils.now() : dto.getStartTime());
        return backTestPortfolio;
    }

    public BackTestPortfolio subtractBalance(BackTestPortfolio backTestPortfolio, BackTestOrder order) {
        BigDecimal newBalance = backTestPortfolio.getBalance().subtract(order.getAmountOfTrade()).setScale(2, RoundingMode.HALF_UP);
        backTestPortfolio.setBalance(newBalance);
        backTestPortfolio.setVersion(backTestPortfolio.getVersion() + 1);
        backTestPortfolio.setLastModificationDate(LocalDateTime.now());
        return backTestPortfolio;
    }

    public BackTestPortfolio addBalance(BackTestPortfolio backTestPortfolio, BackTestOrder order) {
        BigDecimal amountOfTradePlusProfitLoss = order.getAmountOfTrade().add(order.getProfitLoss());
        BigDecimal newBalance = backTestPortfolio.getBalance().add(amountOfTradePlusProfitLoss).setScale(2, RoundingMode.HALF_UP);

        backTestPortfolio.setBalance(newBalance);
        backTestPortfolio.setVersion(backTestPortfolio.getVersion() + 1);
        backTestPortfolio.setLastModificationDate(LocalDateTime.now());
        return backTestPortfolio;
    }
}
