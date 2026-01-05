package app.traderslave.assembler;

import app.traderslave.controller.dto.PatternDetectionReqDto;
import app.traderslave.controller.dto.PatternDetectionResDto;
import app.traderslave.model.Pattern;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.List;

@UtilityClass
public class PatternDetectionAssembler {

    public PatternDetectionResDto toModel(List<Pattern> patterns, PatternDetectionReqDto reqDto) {
        PatternDetectionResDto resDto = new PatternDetectionResDto();
        resDto.setPatterns(toModelList(patterns, reqDto));
        return resDto;
    }

    private List<PatternDetectionResDto.Pattern> toModelList(List<Pattern> patterns, PatternDetectionReqDto reqDto) {
        if (Boolean.TRUE != reqDto.getOnlyBreakoutConfirmed()) {
            return patterns.stream()
                    .map(pattern -> toModel(pattern, reqDto))
                    .toList();
        }

        return patterns.stream()
                .filter(Pattern::isBreakoutConfirmed)
                .map(pattern -> toModel(pattern, reqDto))
                .toList();
    }

    private PatternDetectionResDto.Pattern toModel(Pattern pattern, PatternDetectionReqDto reqDto) {
        PatternDetectionResDto.Pattern dto = new PatternDetectionResDto.Pattern();
        dto.setPatternType(pattern.getPatternType());
        dto.setDirection(pattern.getDirection().name());
        dto.setCategory(pattern.getCategory().name());
        dto.setBreakoutConfirmed(pattern.isBreakoutConfirmed());
        dto.setDescription(pattern.getDescription());
        setCandles(dto, pattern, reqDto);
        return dto;
    }

    private void setCandles(PatternDetectionResDto.Pattern dto, Pattern pattern, PatternDetectionReqDto reqDto) {
        if (CollectionUtils.isEmpty(pattern.getCandles())) {
            return;
        }
        if (Boolean.TRUE == reqDto.getShowAllCandles()) {
            dto.setCandles(pattern.getCandles());
        }
        dto.setLastCandle(pattern.getCandles().get(pattern.getCandles().size() - 1));
    }
}
