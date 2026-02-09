package app.traderslave.command.backtest;

import app.traderslave.assembler.backtest.BackTestCreateOrderAssembler;
import app.traderslave.checker.SimulationChecker;
import app.traderslave.command.base.BaseCommand;
import app.traderslave.domain.model.BackTestPortfolio;
import app.traderslave.domain.model.BackTestPortfolioEvent;
import app.traderslave.domain.model.BackTestOrder;
import app.traderslave.domain.service.BackTestPortfolioEventDomainService;
import app.traderslave.domain.service.BackTestPortfolioDomainService;
import app.traderslave.domain.service.BackTestOrderDomainService;
import app.traderslave.model.dto.req.CandleReqDto;
import app.traderslave.model.dto.req.BackTestCreateOrderReqDto;
import app.traderslave.model.dto.BackTestOrderDto;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.remote.service.BinanceRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackTestCreateOrderCommand extends BaseCommand<BackTestCreateOrderReqDto, BackTestOrderDto> {

    private final BackTestPortfolioDomainService portfolioDomainService;
    private final BackTestOrderDomainService orderDomainService;
    private final BackTestPortfolioEventDomainService portfolioEventDomainService;
    private final BinanceRemoteService binanceRemoteService;

    private final BackTestCreateOrderAssembler createOrderAssembler;

    @Override
    public BackTestOrderDto execute() {
        SimulationChecker.checkLeverage(commandRequest);
        SimulationChecker.checkAmountOfTrade(commandRequest);
        BackTestPortfolio backTestPortfolio = portfolioDomainService.findByIdOrError(commandRequest.getSimulationId());
        SimulationChecker.checkSimulationStatusOpen(backTestPortfolio);
        SimulationChecker.checkBalance(backTestPortfolio, commandRequest);
        BackTestPortfolioEvent latestEvent = portfolioEventDomainService.findLatestEventBySimulationId(backTestPortfolio.getId());
        SimulationChecker.checkRequestTime(backTestPortfolio, latestEvent, commandRequest);
        CandleReqDto candleRequest = adapt(backTestPortfolio.getCurrencyPair(), commandRequest);
        CandleDto candleResponse = binanceRemoteService.findCandleSync(candleRequest);
        BackTestOrder newOrder = orderDomainService.create(backTestPortfolio, commandRequest, candleResponse);
        portfolioDomainService.subtractBalance(backTestPortfolio, newOrder);
        portfolioEventDomainService.create(newOrder, false);
        return createOrderAssembler.toModel(newOrder, commandRequest);
    }

    public CandleReqDto adapt(CurrencyPair currencyPair, BackTestCreateOrderReqDto dto) {
        CandleReqDto reqDto = new CandleReqDto();
        reqDto.setCurrencyPair(currencyPair);
        reqDto.setStartTime(dto.getStartTime());
        reqDto.setRealTimeRequest(dto.isRealTimeRequest());
        return reqDto;
    }
}
