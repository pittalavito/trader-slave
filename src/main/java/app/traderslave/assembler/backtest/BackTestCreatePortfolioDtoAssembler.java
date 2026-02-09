package app.traderslave.assembler.backtest;

import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.model.dto.BackTestCreatePortfolioDto;
import org.springframework.stereotype.Component;

@Component
public class BackTestCreatePortfolioDtoAssembler {

    public BackTestCreatePortfolioDto toModel(BackTestPortfolio backTestPortfolio) {
        return BackTestCreatePortfolioDto.builder()
                .id(backTestPortfolio.getId())
                .currencyPair(backTestPortfolio.getCurrencyPair())
                .description(backTestPortfolio.getDescription())
                .balance(backTestPortfolio.getBalance())
                .currency(backTestPortfolio.getCurrency())
                .build();
    }
}
