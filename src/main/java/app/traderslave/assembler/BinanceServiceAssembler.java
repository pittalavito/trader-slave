package app.traderslave.assembler;

import app.traderslave.controller.dto.CandleResponseDto;
import app.traderslave.controller.dto.CandlesResponseDto;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;
import java.util.List;

@UtilityClass
public class BinanceServiceAssembler {

    public Mono<CandlesResponseDto> toModel(List<CandleResponseDto> responseList) {
        return Mono.just(
                CandlesResponseDto.builder()
                        .list(responseList)
                        .size(responseList.size())
                        .build()
        );
    }
}
