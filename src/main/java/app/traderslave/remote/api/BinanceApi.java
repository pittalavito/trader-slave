package app.traderslave.remote.api;

import app.traderslave.remote.adapter.BinanceResponseAdapter;
import app.traderslave.remote.dto.BinanceGetKlinesRequestDto;
import app.traderslave.remote.dto.BinanceGetKlinesResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class BinanceApi {

    private static final String BASE_URL = "https://api.binance.com";

    private final WebClient webClient;

    public BinanceApi(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public Mono<BinanceGetKlinesResponseDto> getKlines(BinanceGetKlinesRequestDto requestDto) {
        final String URI = "api/v3/klines";
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(URI)
                        .queryParam("symbol", requestDto.getSymbol())
                        .queryParam("interval", requestDto.getInterval())
                        .queryParam("limit", requestDto.getLimit())
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<List<Object>>>() {})
                .map(BinanceResponseAdapter::adaptKlinesResponseDto);
    }
}
