package app.traderslave.command;

import app.traderslave.controller.dto.CandlesRequestDto;
import app.traderslave.controller.dto.CandlesResponseDto;
import app.traderslave.service.BinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetCandlesCommand extends BaseCommand<CandlesRequestDto, Mono<CandlesResponseDto>> {

    private final BinanceService binanceService;

    @Override
    public Mono<CandlesResponseDto> doExecute() {
        return binanceService.getCandleSticks(requestDto);
    }
}
