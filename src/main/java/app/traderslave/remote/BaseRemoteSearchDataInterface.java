package app.traderslave.remote;

import app.traderslave.controller.dto.CandlesReqDto;
import app.traderslave.controller.dto.CandlesResDto;
import reactor.core.publisher.Mono;

public interface BaseRemoteSearchDataInterface {

    Mono<CandlesResDto> getCandleSticks(CandlesReqDto dto);

}
