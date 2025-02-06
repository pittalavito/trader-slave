package app.traderslave.command;

import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.service.BinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetCandlesCommand extends BaseCommand<CandlesReqDto, Mono<CandlesResDto>> {

    private final BinanceService binanceService;

    @Override
    public Mono<CandlesResDto> doExecute() {
        return binanceService.getCandleSticks(requestDto);
    }
}
