package app.traderslave.remote.api;

import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class BinanceApi {

    private static final String BASE_URL = "https://api.binance.com";
    private static final String KLINES_URL = "api/v3/klines";

    private final WebClient webClient;

    public BinanceApi(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public Mono<List<Object[]>> getKlines(BinanceGetKlinesRequestDto requestDto) {
        //todo creare le eccezioni customizzate
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
                        .flatMap(body -> Mono.error(new RuntimeException(body)))
                )
                .bodyToMono(new ParameterizedTypeReference<List<Object[]>>() {})
                .onErrorMap(e -> new RuntimeException(KLINES_URL + " error: " + e.getMessage()));

    }
}
