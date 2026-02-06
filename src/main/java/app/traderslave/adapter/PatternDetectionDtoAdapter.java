package app.traderslave.adapter;

import app.traderslave.model.dto.SimulationPatterStrategyDto;
import app.traderslave.model.dto.req.PatternDetectionReqDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class PatternDetectionDtoAdapter {

    public PatternDetectionReqDto adapt(SimulationPatterStrategyDto dto, LocalDateTime endTime, boolean onlyBreakoutConfirmed) {
        var patternDetection = new PatternDetectionReqDto();
        patternDetection.setCurrencyPair(dto.getCurrencyPair());
        patternDetection.setStartTime(dto.getSimulationStartTime());
        patternDetection.setEndTime(endTime);
        patternDetection.setTimeFrame(dto.getPatternDetectionTimeFrame());
        patternDetection.setOnlyBreakoutConfirmed(onlyBreakoutConfirmed);
        return patternDetection;
    }
}
