package app.traderslave.controller;

import app.traderslave.model.dto.req.BackTestCloseOrderReqDto;
import app.traderslave.model.dto.req.BackTestClosePortfolioReqDto;
import app.traderslave.model.dto.req.BackTestCreateOrderReqDto;
import app.traderslave.model.dto.req.BackTestCreatePortfolioReqDto;
import app.traderslave.model.dto.CloseBackTestPortfolioDto;
import app.traderslave.model.dto.BackTestCreatePortfolioDto;
import app.traderslave.model.dto.BackTestOrderDto;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.OrderType;
import app.traderslave.service.old.SimulationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(BackTestPortfolioController.class)
class BackTestBasePortfolioAgentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SimulationService simulationService;

    @Test
    void testCreateSimulation() {
        BackTestCreatePortfolioReqDto backTestCreatePortfolioReqDto = new BackTestCreatePortfolioReqDto();
        backTestCreatePortfolioReqDto.setCurrencyPair(CurrencyPair.BTC_USDT);
        backTestCreatePortfolioReqDto.setStartTime(LocalDateTime.now());
        backTestCreatePortfolioReqDto.setDescription("Description");

        BackTestCreatePortfolioDto backTestCreatePortfolioDto = BackTestCreatePortfolioDto.builder().build();

        Mockito.when(simulationService.create(any(BackTestCreatePortfolioReqDto.class))).thenReturn(Mono.just(backTestCreatePortfolioDto));

        webTestClient.post()
                .uri("/simulation")
                .bodyValue(backTestCreatePortfolioReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BackTestCreatePortfolioDto.class)
                .isEqualTo(backTestCreatePortfolioDto);
    }

    @Test
    void testCloseSimulation() {
        BackTestClosePortfolioReqDto backTestClosePortfolioReqDto = new BackTestClosePortfolioReqDto();
        backTestClosePortfolioReqDto.setSimulationId(1L);
        backTestClosePortfolioReqDto.setStartTime(LocalDateTime.now());

        CloseBackTestPortfolioDto closeBackTestPortfolioDto = CloseBackTestPortfolioDto.builder().build();

        Mockito.when(simulationService.close(any(BackTestClosePortfolioReqDto.class))).thenReturn(Mono.just(closeBackTestPortfolioDto));

        webTestClient.put()
                .uri("/simulation")
                .bodyValue(backTestClosePortfolioReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CloseBackTestPortfolioDto.class)
                .isEqualTo(closeBackTestPortfolioDto);
    }

    @Test
    void testDeleteAllSimulations() {
        webTestClient.delete()
                .uri("/simulation/all")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreateOrder() {
        BackTestCreateOrderReqDto backTestCreateOrderReqDto = new BackTestCreateOrderReqDto();
        backTestCreateOrderReqDto.setAmountOfTrade(BigDecimal.TEN);
        backTestCreateOrderReqDto.setStartTime(LocalDateTime.now());
        backTestCreateOrderReqDto.setOrderType(OrderType.BUY);
        backTestCreateOrderReqDto.setSimulationId(1L);

        BackTestOrderDto backTestOrderDto = BackTestOrderDto.builder().build();

        Mockito.when(simulationService.createOrder(any(BackTestCreateOrderReqDto.class))).thenReturn(Mono.just(backTestOrderDto));

        webTestClient.post()
                .uri("/simulation/order")
                .bodyValue(backTestCreateOrderReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BackTestOrderDto.class)
                .isEqualTo(backTestOrderDto);
    }

    @Test
    void testCloseOrder() {
        BackTestCloseOrderReqDto backTestCloseOrderReqDto = new BackTestCloseOrderReqDto();
        backTestCloseOrderReqDto.setSimulationId(1L);
        backTestCloseOrderReqDto.setOrderId(1L);
        backTestCloseOrderReqDto.setStartTime(LocalDateTime.now());

        BackTestOrderDto backTestOrderDto = BackTestOrderDto.builder().build();

        Mockito.when(simulationService.closeOrder(any(BackTestCloseOrderReqDto.class))).thenReturn(Mono.just(backTestOrderDto));

        webTestClient.put()
                .uri("/simulation/order")
                .bodyValue(backTestCloseOrderReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BackTestOrderDto.class)
                .isEqualTo(backTestOrderDto);
    }
}