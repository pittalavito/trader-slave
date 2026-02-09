package app.traderslave.assembler;

import app.traderslave.model.dto.req.PatternDetectionReqDto;
import app.traderslave.model.dto.PatternDetectionDto;
import app.traderslave.model.dto.CandleDto;
import app.traderslave.model.dto.PatternDto;
import app.traderslave.domain.model.BackTestBinanceCandle;
import app.traderslave.utils.PatternUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PatternDetectionAssembler {

    public PatternDetectionDto toModelBackTest(List<BackTestBinanceCandle> candles, PatternDetectionReqDto dto) {
        if (!CollectionUtils.isEmpty(candles)) {
            return toModel(adapt(candles), dto);
        }
        return new PatternDetectionDto();
    }

    public PatternDetectionDto toModel(List<CandleDto> candles, PatternDetectionReqDto dto) {
        PatternDetectionDto result = new PatternDetectionDto();

        if (!CollectionUtils.isEmpty(candles)) {
            List<PatternDto> patternsDto = PatternUtils.detectPatterns(candles, dto);
            CandleDto lastCandle = candles.get(candles.size() - 1);

            result.setPatterns(toModelPatterns(patternsDto, dto));
            result.setCloseTime(lastCandle.getCloseTime());
            result.setClose(lastCandle.getClose());
        }
        return result;
    }

    private List<PatternDetectionDto.Pattern> toModelPatterns(List<PatternDto> patternsDto, PatternDetectionReqDto dto) {
        return patternsDto.stream()
                .filter(patternDto -> filter(patternDto, dto))
                .map(patternDto -> toModel(patternDto, dto))
                .sorted(this::sortByCloseTimeDesc)
                .toList();
    }

    private PatternDetectionDto.Pattern toModel(PatternDto patternDto, PatternDetectionReqDto dto) {
        PatternDetectionDto.Pattern result = new PatternDetectionDto.Pattern();
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

    private List<PatternDetectionDto.Candle> toModel(List<CandleDto> candles) {
        return candles.stream()
                .map(this::toModel)
                .toList();
    }

    private PatternDetectionDto.Candle toModel(CandleDto candle) {
        PatternDetectionDto.Candle dto = new PatternDetectionDto.Candle();
        dto.setCloseTime(candle.getCloseTime());
        dto.setClose(candle.getClose());
        return dto;
    }

    /** Filters patterns based on request criteria */
    private boolean filter(PatternDto patternDto, PatternDetectionReqDto dto) {
        return Boolean.TRUE != dto.getOnlyBreakoutConfirmed() || patternDto.isBreakoutConfirmed();
    }

    /** Filters candles in the pattern based on request criteria */
    private void filter(PatternDetectionDto.Pattern patternRto, PatternDetectionReqDto reqDto) {
        if (Boolean.TRUE != reqDto.getShowAllCandles()) {
            patternRto.setCandles(null);
        }
    }

    /** Sorts patterns by the close time of their last candle in descending order */
    private int sortByCloseTimeDesc(PatternDetectionDto.Pattern p1, PatternDetectionDto.Pattern p2) {
        LocalDateTime dt1 = p1.getLastCandle().getCloseTime();
        LocalDateTime dt2 = p2.getLastCandle().getCloseTime();
        return dt2.compareTo(dt1);
    }

    public List<CandleDto> adapt(List<BackTestBinanceCandle> tests) {
        return tests.stream()
                .map(this::adapt)
                .toList();
    }

    public CandleDto adapt(BackTestBinanceCandle test) {
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
