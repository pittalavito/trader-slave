package app.traderslave.command.backtest;

import app.traderslave.assembler.backtest.BackTestCloseOrderAssembler;
import app.traderslave.checker.BackTestPortfolioChecker;
import app.traderslave.command.base.BaseCommand;
import app.traderslave.model.dto.req.CandleReqDto;
import app.traderslave.model.dto.req.BackTestCloseOrderReqDto;
import app.traderslave.model.dto.req.BackTestCreateOrderReqDto;
import app.traderslave.model.dto.BackTestOrderDto;
import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.model.BackTestPortfolioEvent;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.domain.service.BackTestPortfolioEventDomainService;
import app.traderslave.domain.service.BackTestPortfolioDomainService;
import app.traderslave.domain.service.BackTestOrderDomainService;
import app.traderslave.model.dto.OrderReportDto;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.service.BackTestOrderReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackTestCloseOrderCommand extends BaseCommand<BackTestCloseOrderReqDto, BackTestOrderDto> {

    private final BackTestPortfolioDomainService portfolioDomainService;
    private final BackTestOrderDomainService orderDomainService;
    private final BackTestPortfolioEventDomainService portfolioEventDomainService;
    private final BackTestOrderReportService orderReportService;

    private final BackTestCloseOrderAssembler closeOrderAssembler;

    @Override
    public BackTestOrderDto execute() {
        BackTestPortfolio backTestPortfolio = portfolioDomainService.findByIdOrError(commandRequest.getSimulationId());
        BackTestPortfolioChecker.checkPortfolioStatusOpen(backTestPortfolio);
        BackTestPortfolioEvent latestEvent = portfolioEventDomainService.findLatestEventBySimulationId(backTestPortfolio.getId());
        BackTestPortfolioChecker.checkRequestTime(backTestPortfolio, latestEvent, commandRequest);
        BackTestOrder order = orderDomainService.findByIdAndSimulationIdOrError(commandRequest.getOrderId(), backTestPortfolio.getId());
        BackTestPortfolioChecker.checkOrderStatusOpen(order);
        OrderReportDto report = orderReportService.createShortTermReport(backTestPortfolio, order, commandRequest);
        BackTestOrder closedOrder = orderDomainService.close(order, report, false);
        portfolioDomainService.addBalance(backTestPortfolio, closedOrder);
        portfolioEventDomainService.create(closedOrder, false);
        return closeOrderAssembler.toModel(closedOrder, commandRequest);
    }

    public CandleReqDto adapt(CurrencyPair currencyPair, BackTestCreateOrderReqDto dto) {
        CandleReqDto reqDto = new CandleReqDto();
        reqDto.setCurrencyPair(currencyPair);
        reqDto.setStartTime(dto.getStartTime());
        reqDto.setRealTimeRequest(dto.isRealTimeRequest());
        return reqDto;
    }
}
