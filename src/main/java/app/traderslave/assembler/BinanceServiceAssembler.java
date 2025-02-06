package app.traderslave.assembler;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CandlesResDto;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;
import java.util.List;

@UtilityClass
public class BinanceServiceAssembler {

    public Mono<CandlesResDto> toModel(List<CandleResDto> responseList) {
        return Mono.just(
                CandlesResDto.builder()
                        .list(responseList)
                        .size(responseList.size())
                        .build()
        );
    }
}
