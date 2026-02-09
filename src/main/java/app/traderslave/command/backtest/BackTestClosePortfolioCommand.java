package app.traderslave.command.backtest;

import app.traderslave.assembler.backtest.BackTestCloseOrderAssembler;
import app.traderslave.assembler.backtest.BackTestClosePortfolioAssembler;
import app.traderslave.checker.SimulationChecker;
import app.traderslave.command.base.BaseCommand;
import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.model.BackTestPortfolioEvent;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.domain.service.BackTestPortfolioEventDomainService;
import app.traderslave.domain.service.BackTestPortfolioDomainService;
import app.traderslave.domain.service.BackTestOrderDomainService;
import app.traderslave.model.dto.req.BackTestClosePortfolioReqDto;
import app.traderslave.model.dto.CloseBackTestPortfolioDto;
import app.traderslave.model.dto.BackTestOrderDto;
import app.traderslave.model.dto.OrderReportDto;
import app.traderslave.service.BackTestOrderReportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@RequiredArgsConstructor
public class BackTestClosePortfolioCommand extends BaseCommand<BackTestClosePortfolioReqDto, CloseBackTestPortfolioDto> {

    private final BackTestPortfolioDomainService portfolioDomainService;
    private final BackTestOrderDomainService orderDomainService;
    private final BackTestPortfolioEventDomainService portfolioEventDomainService;
    private final BackTestOrderReportService orderReportService;

    private final BackTestCloseOrderAssembler closeOrderAssembler;
    private final BackTestClosePortfolioAssembler closePortfolioAssembler;

    @Override
    @Transactional
    public CloseBackTestPortfolioDto execute() {
        BackTestPortfolio backTestPortfolio = portfolioDomainService.findByIdOrError(commandRequest.getSimulationId());
        SimulationChecker.checkSimulationStatusOpen(backTestPortfolio);
        BackTestPortfolioEvent latestEvent = portfolioEventDomainService.findLatestEventBySimulationId(backTestPortfolio.getId());
        SimulationChecker.checkRequestTime(backTestPortfolio, latestEvent, commandRequest);

        Map<Long, BackTestOrderDto> ordersIdsMap = new HashMap<>();
        Map<BackTestOrderDto.Status, List<Long>> ordersIdsStatusMap = new EnumMap<>(BackTestOrderDto.Status.class);
        Arrays.stream(BackTestOrderDto.Status.values()).forEach(status -> ordersIdsStatusMap.put(status, new ArrayList<>()));

        List<BackTestOrder> orders = orderDomainService.findAllBySimulationId(backTestPortfolio.getId());
        if (!CollectionUtils.isEmpty(orders)) {
            orders.forEach(order -> {
                BackTestOrderDto resDto;
                if (order.isOpen()) {
                    OrderReportDto report = orderReportService.createShortTermReport(backTestPortfolio, order, commandRequest);
                    BackTestOrder closedOrder = orderDomainService.close(order, report, true);
                    portfolioDomainService.subtractBalance(backTestPortfolio, closedOrder);
                    portfolioEventDomainService.create(closedOrder, true);
                    order = closedOrder;
                }
                resDto = closeOrderAssembler.toModel(order, commandRequest);
                ordersIdsMap.put(order.getId(), resDto);
                ordersIdsStatusMap.get(resDto.getStatus()).add(order.getId());
            });
        }

        List<BackTestPortfolioEvent> events = portfolioEventDomainService.findBySimulationIdOrderByEventTimeAsc(backTestPortfolio.getId());
        BackTestPortfolio closeBackTestPortfolio = portfolioDomainService.close(backTestPortfolio, commandRequest);
        return closePortfolioAssembler.toModel(closeBackTestPortfolio, ordersIdsMap, ordersIdsStatusMap, events);
    }
}
