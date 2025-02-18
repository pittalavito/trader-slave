package app.traderslave.factory;

import app.traderslave.controller.dto.PostSimulationReqDto;
import app.traderslave.model.domain.Simulation;
import app.traderslave.model.enums.Currency;
import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class SimulationEntityFactory {

    private final BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(100);
    private final Currency DEFAULT_CURRENCY = Currency.USDT;

    public Simulation create(PostSimulationReqDto reqDto) {
        return Simulation.builder()
                .currencyPair(reqDto.getCurrencyPair())
                .startDate(reqDto.getStartDate())
                .endDate(reqDto.getEndDate())
                .currency(DEFAULT_CURRENCY)
                .timeFrame(reqDto.getTimeFrame())
                .balance(DEFAULT_BALANCE)
                .uid(UUID.randomUUID().toString())
                .version(0)
                .build();
    }
}
