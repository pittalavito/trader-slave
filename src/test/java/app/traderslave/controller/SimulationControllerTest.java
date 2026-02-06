package app.traderslave.controller;

import app.traderslave.model.dto.req.CloseSimulationOrderReqDto;
import app.traderslave.model.dto.req.CloseSimulationReqDto;
import app.traderslave.model.dto.req.CreateSimulationOrderReqDto;
import app.traderslave.model.dto.req.CreateSimulationReqDto;
import app.traderslave.model.dto.CloseSimulationDto;
import app.traderslave.model.dto.CreateSimulationDto;
import app.traderslave.model.dto.SimulationOrderDto;
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

        CreateSimulationDto createSimulationDto = CreateSimulationDto.builder().build();

        Mockito.when(simulationService.create(any(CreateSimulationReqDto.class))).thenReturn(Mono.just(createSimulationDto));

        webTestClient.post()
                .uri("/simulation")
                .bodyValue(createSimulationReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CreateSimulationDto.class)
                .isEqualTo(createSimulationDto);
    }

    @Test
    void testCloseSimulation() {
        CloseSimulationReqDto closeSimulationReqDto = new CloseSimulationReqDto();
        closeSimulationReqDto.setSimulationId(1L);
        closeSimulationReqDto.setStartTime(LocalDateTime.now());

        CloseSimulationDto closeSimulationDto = CloseSimulationDto.builder().build();

        Mockito.when(simulationService.close(any(CloseSimulationReqDto.class))).thenReturn(Mono.just(closeSimulationDto));

        webTestClient.put()
                .uri("/simulation")
                .bodyValue(closeSimulationReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CloseSimulationDto.class)
                .isEqualTo(closeSimulationDto);
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

        SimulationOrderDto simulationOrderDto = SimulationOrderDto.builder().build();

        Mockito.when(simulationService.createOrder(any(CreateSimulationOrderReqDto.class))).thenReturn(Mono.just(simulationOrderDto));

        webTestClient.post()
                .uri("/simulation/order")
                .bodyValue(createSimulationOrderReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SimulationOrderDto.class)
                .isEqualTo(simulationOrderDto);
    }

    @Test
    void testCloseOrder() {
        CloseSimulationOrderReqDto closeSimulationOrderReqDto = new CloseSimulationOrderReqDto();
        closeSimulationOrderReqDto.setSimulationId(1L);
        closeSimulationOrderReqDto.setOrderId(1L);
        closeSimulationOrderReqDto.setStartTime(LocalDateTime.now());

        SimulationOrderDto simulationOrderDto = SimulationOrderDto.builder().build();

        Mockito.when(simulationService.closeOrder(any(CloseSimulationOrderReqDto.class))).thenReturn(Mono.just(simulationOrderDto));

        webTestClient.put()
                .uri("/simulation/order")
                .bodyValue(closeSimulationOrderReqDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SimulationOrderDto.class)
                .isEqualTo(simulationOrderDto);
    }
}