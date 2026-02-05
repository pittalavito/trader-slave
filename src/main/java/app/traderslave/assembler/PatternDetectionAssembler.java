package app.traderslave.assembler;

import app.traderslave.model.dto.req.PatternDetectionReqDto;
import app.traderslave.model.dto.res.PatternDetectionResDto;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.dto.PatternDto;
import app.traderslave.domain.model.CandleBackTest;
import app.traderslave.utils.PatternUtils;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class PatternDetectionAssembler {

    public PatternDetectionResDto toModelBackTest(List<CandleBackTest> candles, PatternDetectionReqDto dto) {
        if (!CollectionUtils.isEmpty(candles)) {
            return toModel(adapt(candles), dto);
        }
        return new PatternDetectionResDto();
    }

    public PatternDetectionResDto toModel(List<CandleDto> candles, PatternDetectionReqDto dto) {
        PatternDetectionResDto result = new PatternDetectionResDto();

        if (!CollectionUtils.isEmpty(candles)) {
            List<PatternDto> patternDtos = PatternUtils.detectPatterns(candles, dto);
            CandleDto lastCandle = candles.get(candles.size() - 1);

            result.setPatterns(toModelPatterns(patternDtos, dto));
            result.setCloseTime(lastCandle.getCloseTime());
            result.setClose(lastCandle.getClose());
        }
        return result;
    }

    private List<PatternDetectionResDto.Pattern> toModelPatterns(List<PatternDto> patternDtos, PatternDetectionReqDto dto) {
        return patternDtos.stream()
                .filter(patternDto -> filter(patternDto, dto))
                .map(patternDto -> toModel(patternDto, dto))
                .sorted(PatternDetectionAssembler::sortByCloseTimeDesc)
                .toList();
    }

    private PatternDetectionResDto.Pattern toModel(PatternDto patternDto, PatternDetectionReqDto dto) {
        PatternDetectionResDto.Pattern result = new PatternDetectionResDto.Pattern();
        result.setPatternType(patternDto.getPatternType());
        result.setDirection(patternDto.getDirection().name());
        result.setCategory(patternDto.getCategory().name());
        result.setBreakoutConfirmed(patternDto.isBreakoutConfirmed());
        result.setDescription(patternDto.getDescription());

        if (!CollectionUtils.isEmpty(patternDto.getCandles())) {
            var candles = toModel(patternDto.getCandles());
            result.setCandles(candles);
            result.setLastCandle(candles.get(candles.size() - 1));
            filter(result, dto);
        }
        return result;
    }

    private List<PatternDetectionResDto.Candle> toModel(List<CandleDto> candles) {
        return candles.stream()
                .map(PatternDetectionAssembler::toModel)
                .toList();
    }

    private PatternDetectionResDto.Candle toModel(CandleDto candle) {
        PatternDetectionResDto.Candle dto = new PatternDetectionResDto.Candle();
        dto.setCloseTime(candle.getCloseTime());
        dto.setClose(candle.getClose());
        return dto;
    }

    /** Filters patterns based on request criteria */
    private boolean filter(PatternDto patternDto, PatternDetectionReqDto dto) {
        return Boolean.TRUE != dto.getOnlyBreakoutConfirmed() || patternDto.isBreakoutConfirmed();
    }

    /** Filters candles in the pattern based on request criteria */
    private void filter(PatternDetectionResDto.Pattern patternRto, PatternDetectionReqDto reqDto) {
        if (Boolean.TRUE != reqDto.getShowAllCandles()) {
            patternRto.setCandles(null);
        }
    }

    /** Sorts patterns by the close time of their last candle in descending order */
    private int sortByCloseTimeDesc(PatternDetectionResDto.Pattern p1, PatternDetectionResDto.Pattern p2) {
        LocalDateTime dt1 = p1.getLastCandle().getCloseTime();
        LocalDateTime dt2 = p2.getLastCandle().getCloseTime();
        return dt2.compareTo(dt1);
    }

    public List<CandleDto> adapt(List<CandleBackTest> tests) {
        return tests.stream()
                .map(PatternDetectionAssembler::adapt)
                .toList();
    }

    public CandleDto adapt(CandleBackTest test) {
        CandleDto resDto = new CandleDto();
        resDto.setOpenTime(test.getOpenTime());
        resDto.setCloseTime(test.getCloseTime());
        resDto.setOpen(test.getOpen());
        resDto.setHigh(test.getHigh());
        resDto.setLow(test.getLow());
        resDto.setClose(test.getClose());
        resDto.setVolume(test.getVolume());
        return resDto;
    }
}
