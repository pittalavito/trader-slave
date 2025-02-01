package app.traderslave.remote;

import app.traderslave.controller.dto.CandlesRequestDto;
import app.traderslave.controller.dto.CandlesResponseDto;
import reactor.core.publisher.Mono;

public interface BaseRemoteTrainingInterface {

    Mono<CandlesResponseDto> getCandleSticks(CandlesRequestDto dto);

}
