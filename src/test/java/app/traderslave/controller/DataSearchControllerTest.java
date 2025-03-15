package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import app.traderslave.service.BinanceService;
import app.traderslave.remote.api.BinanceApi;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(DataSearchController.class)
class DataSearchControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BinanceService binanceService;

    @MockitoBean
    private BinanceApi binanceApi;

    @Test
    void testGetCandlesOk() {
        CandlesResDto candlesResDto = CandlesResDto.builder().build();

        Mockito.when(binanceService.findCandles(any(CandlesReqDto.class))).thenReturn(Mono.just(candlesResDto));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data-search/candles")
                        .queryParam(CandlesReqDto.Fields.currencyPair, CurrencyPair.BTC_USDT)
                        .queryParam(CandlesReqDto.Fields.timeFrame, TimeFrame.ONE_MINUTE)
                        .queryParam(CandlesReqDto.Fields.startTime, LocalDateTime.now())
                        .queryParam(CandlesReqDto.Fields.endTime, LocalDateTime.now().plusDays(1))
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(CandlesResDto.class)
                .isEqualTo(candlesResDto);
    }

    @Test
    void testGetCandleOk() {
        CandleResDto candleResDto = CandleResDto.builder().build();

        Mockito.when(binanceService.findCandle(any(CandleReqDto.class))).thenReturn(Mono.just(candleResDto));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data-search/candle")
                        .queryParam(CandleReqDto.Fields.currencyPair, CurrencyPair.BTC_USDT)
                        .queryParam(TimeReqDto.Fields.startTime, LocalDateTime.now())
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(CandleResDto.class)
                .isEqualTo(candleResDto);
    }
}