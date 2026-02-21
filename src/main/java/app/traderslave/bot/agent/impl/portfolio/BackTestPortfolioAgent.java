package app.traderslave.bot.agent.impl.portfolio;

import app.traderslave.bot.agent.assembler.BackTestPortfolioAssembler;
import app.traderslave.bot.BotConfig;
import app.traderslave.bot.agent.dto.PortfolioAgentDto;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.service.BackTestOrderService;
import app.traderslave.service.BackTestPortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BackTestPortfolioAgent extends BasePortfolioAgent<Long> {

    private final BackTestPortfolioService portfolioService;
    private final BackTestOrderService orderService;
    private final BackTestPortfolioAssembler portfolioAssembler;

    @Override
    public PortfolioAgentDto create(BotConfig request) {
        //todo implement
        return null;
    }

    @Override
    public PortfolioAgentDto get(Long request) {
        var entity = portfolioService.get(request);
        var orders = orderService.getAll(request, BackTestOrder.Status.OPEN);
        return portfolioAssembler.toModel(entity, orders);
    }
}
