package app.traderslave.bot.agent.assembler;

import app.traderslave.bot.agent.model.PortfolioModel;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.domain.model.BackTestPortfolio;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
public class BackTestPortfolioAssembler {

    public PortfolioModel toModel(BackTestPortfolio portfolio, List<BackTestOrder> openOrders) {
        var orderModels = toModel(openOrders, portfolio);
        var allocatedCapital = orderModels.stream()
                .map(PortfolioModel.Order::getAllocatedCapital)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return PortfolioModel.builder()
                .id(portfolio.getId())
                .capital(portfolio.getBalance())
                .allocatedCapital(allocatedCapital)
                .allTimeHigh(null)
                .openOrders(orderModels)
                .build();
    }


    private List<PortfolioModel.Order> toModel(List<BackTestOrder> orders, BackTestPortfolio portfolio) {
        return orders.stream()
                .map(order -> toModel(order, portfolio))
                .toList();
    }

    private PortfolioModel.Order toModel(BackTestOrder order, BackTestPortfolio portfolio) {
        return PortfolioModel.Order.builder()
                .currencyPair(portfolio.getCurrencyPair())
                .orderType(order.getType())
                .allocatedCapital(order.getAmountOfTrade())
                .openTime(order.getOpenTime())
                .entryPrice(order.getOpenPrice())
                //todo magari vengono valorizzati in un secondo momento (capiamo)
                //.quantity(null)
                //.currentPrice(null)
                //.profitLossInPercent(null)
                //.durationInMinutes(null)
                //.openSignal(null)
                .build();
    }
}
