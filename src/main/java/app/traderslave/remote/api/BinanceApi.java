package app.traderslave.remote.api;

import app.traderslave.exception.custom.BinanceRemoteException;
import app.traderslave.model.domain.BinanceCandle;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import app.traderslave.repository.BinanceCandleRepository;
import app.traderslave.service.BinanceCandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class BinanceApi {

    private static final String BASE_URL = "https://api.binance.com";
    private static final String KLINES_URL = "api/v3/klines";

    private final WebClient webClient;
    private final BinanceCandleService binanceCandleService;

    public BinanceApi(WebClient.Builder webClientBuilder, BinanceCandleRepository binanceCandleRepository) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
        this.binanceCandleService = new BinanceCandleService(binanceCandleRepository);
    }

    public Mono<List<Object[]>> getKlines(BinanceGetKlinesRequestDto requestDto) {
        Optional<BinanceCandle> cachedCandleOpt = binanceCandleService.findByUid(requestDto);
        if (cachedCandleOpt.isPresent()) {
            var cache = cachedCandleOpt.get();
            log.info("Cache hit for key: {}", cache.getUid());
            return Mono.just(cache.getCandles());
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(KLINES_URL)
                        .queryParam("symbol", requestDto.getSymbol())
                        .queryParam("interval", requestDto.getInterval())
                        .queryParam("limit", requestDto.getLimit())
                        .queryParam("startTime", requestDto.getStartTime())
                        .queryParam("endTime", requestDto.getEndTime())
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> res
                        .bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RemoteException(body)))
                )
                .bodyToMono(new ParameterizedTypeReference<List<Object[]>>() {})
                .onErrorMap(e -> new BinanceRemoteException(e.getMessage(), KLINES_URL))
                .doOnNext(response -> binanceCandleService.add(requestDto, response));
    }
}
