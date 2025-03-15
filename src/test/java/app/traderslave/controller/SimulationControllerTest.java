package app.traderslave.controller;

import app.traderslave.controller.dto.*;
import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.OrderType;
import app.traderslave.service.SimulationService;
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

@WebFluxTest(SimulationController.class)
class SimulationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SimulationService simulationService;

    @Test
    void testCreateSimulation() {
        CreateSimulationReqDto createSimulationReqDto = new CreateSimulationReqDto();
        createSimulationReqDto.setCurrencyPair(CurrencyPair.BTC_USDT);
        createSimulationReqDto.setStartTime(LocalDateTime.now());
        createSimulationReqDto.setDescription("Description");

        PostSimulationResDto postSimulationResDto = PostSimulationResDto.builder().build();

        Mockito.when(simulationService.create(any(CreateSimulationReqDto.class))).thenReturn(Mono.just(postSimulationResDto));

        webTestClient.post()
                .uri("/simulation")
                .bodyValue(createSimulationReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PostSimulationResDto.class)
                .isEqualTo(postSimulationResDto);
    }

    @Test
    void testCloseSimulation() {
        CloseSimulationReqDto closeSimulationReqDto = new CloseSimulationReqDto();
        closeSimulationReqDto.setSimulationId(1L);
        closeSimulationReqDto.setStartTime(LocalDateTime.now());

        CloseSimulationResDto closeSimulationResDto = CloseSimulationResDto.builder().build();

        Mockito.when(simulationService.close(any(CloseSimulationReqDto.class))).thenReturn(Mono.just(closeSimulationResDto));

        webTestClient.put()
                .uri("/simulation")
                .bodyValue(closeSimulationReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CloseSimulationResDto.class)
                .isEqualTo(closeSimulationResDto);
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
        CreateSimulationOrderReqDto createSimulationOrderReqDto = new CreateSimulationOrderReqDto();
        createSimulationOrderReqDto.setAmountOfTrade(BigDecimal.TEN);
        createSimulationOrderReqDto.setStartTime(LocalDateTime.now());
        createSimulationOrderReqDto.setOrderType(OrderType.BUY);
        createSimulationOrderReqDto.setSimulationId(1L);

        SimulationOrderResDto simulationOrderResDto = SimulationOrderResDto.builder().build();

        Mockito.when(simulationService.createOrder(any(CreateSimulationOrderReqDto.class))).thenReturn(Mono.just(simulationOrderResDto));

        webTestClient.post()
                .uri("/simulation/order")
                .bodyValue(createSimulationOrderReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SimulationOrderResDto.class)
                .isEqualTo(simulationOrderResDto);
    }

    @Test
    void testCloseOrder() {
        CloseSimulationOrderReqDto closeSimulationOrderReqDto = new CloseSimulationOrderReqDto();
        closeSimulationOrderReqDto.setSimulationId(1L);
        closeSimulationOrderReqDto.setOrderId(1L);
        closeSimulationOrderReqDto.setStartTime(LocalDateTime.now());

        SimulationOrderResDto simulationOrderResDto = SimulationOrderResDto.builder().build();

        Mockito.when(simulationService.closeOrder(any(CloseSimulationOrderReqDto.class))).thenReturn(Mono.just(simulationOrderResDto));

        webTestClient.put()
                .uri("/simulation/order")
                .bodyValue(closeSimulationOrderReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SimulationOrderResDto.class)
                .isEqualTo(simulationOrderResDto);
    }
}