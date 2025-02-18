package app.traderslave.service;

import app.traderslave.assembler.SimulationServiceAssembler;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.controller.dto.PostSimulationReqDto;
import app.traderslave.controller.dto.PostSimulationResDto;
import app.traderslave.factory.SimulationEntityFactory;
import app.traderslave.repository.SimulationRepository;
import app.traderslave.model.domain.Simulation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SimulationService {

    private final BinanceService binanceService;
    private final SimulationRepository repository;

    @Transactional
    public Mono<PostSimulationResDto> create(PostSimulationReqDto reqDto) {
        Mono<CandlesResDto> candles = binanceService.getCandleSticks(reqDto);
        Simulation simulation = repository.save(SimulationEntityFactory.create(reqDto));
        return SimulationServiceAssembler.toModel(candles, simulation);
    }
}
