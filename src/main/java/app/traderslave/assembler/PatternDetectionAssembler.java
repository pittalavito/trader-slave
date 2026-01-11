package app.traderslave.assembler;

import app.traderslave.controller.dto.CandleResDto;
import app.traderslave.controller.dto.CandlesResDto;
import app.traderslave.controller.dto.PatternDetectionReqDto;
import app.traderslave.controller.dto.PatternDetectionResDto;
import app.traderslave.model.Pattern;
import app.traderslave.utility.PatternUtils;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class PatternDetectionAssembler {

    public PatternDetectionResDto toModel(CandlesResDto candles, PatternDetectionReqDto dto) {
        PatternDetectionResDto result = new PatternDetectionResDto();

        if (!CollectionUtils.isEmpty(candles.getList())) {
            List<Pattern> patterns = PatternUtils.detectPatterns(candles.getList(), dto);
            CandleResDto lastCandle = candles.getList().get(candles.getSize() - 1);

            result.setPatterns(toModel(patterns, dto));
            result.setCloseTime(lastCandle.getCloseTime());
            result.setClose(lastCandle.getClose());
        }
        return result;
    }

    private List<PatternDetectionResDto.Pattern> toModel(List<Pattern> patterns, PatternDetectionReqDto dto) {
        return patterns.stream()
                .filter(pattern -> filter(pattern, dto))
                .map(pattern -> toModel(pattern, dto))
                .sorted(PatternDetectionAssembler::sortByCloseTimeDesc)
                .toList();
    }

    private PatternDetectionResDto.Pattern toModel(Pattern pattern, PatternDetectionReqDto dto) {
        PatternDetectionResDto.Pattern result = new PatternDetectionResDto.Pattern();
        result.setPatternType(pattern.getPatternType());
        result.setDirection(pattern.getDirection().name());
        result.setCategory(pattern.getCategory().name());
        result.setBreakoutConfirmed(pattern.isBreakoutConfirmed());
        result.setDescription(pattern.getDescription());

        if (!CollectionUtils.isEmpty(pattern.getCandles())) {
            var candles = toModel(pattern.getCandles());
            result.setCandles(candles);
            result.setLastCandle(candles.get(candles.size() - 1));
            filter(result, dto);
        }
        return result;
    }

    private List<PatternDetectionResDto.Candle> toModel(List<CandleResDto> candles) {
        return candles.stream()
                .map(PatternDetectionAssembler::toModel)
                .toList();
    }

    private PatternDetectionResDto.Candle toModel(CandleResDto candle) {
        PatternDetectionResDto.Candle dto = new PatternDetectionResDto.Candle();
        dto.setCloseTime(candle.getCloseTime());
        dto.setClose(candle.getClose());
        return dto;
    }

    /** Filters patterns based on request criteria */
    private boolean filter(Pattern pattern, PatternDetectionReqDto dto) {
        return Boolean.TRUE != dto.getOnlyBreakoutConfirmed() || pattern.isBreakoutConfirmed();
    }

    /** Filters candles in the pattern based on request criteria */
    private void filter(PatternDetectionResDto.Pattern patternRto, PatternDetectionReqDto reqDto) {
        if (Boolean.TRUE == reqDto.getShowAllCandles()) {
            patternRto.setCandles(null);
        }
    }

    private int sortByCloseTimeDesc(PatternDetectionResDto.Pattern p1, PatternDetectionResDto.Pattern p2) {
        LocalDateTime dt1 = p1.getLastCandle().getCloseTime();
        LocalDateTime dt2 = p2.getLastCandle().getCloseTime();
        return dt2.compareTo(dt1);
    }
}
