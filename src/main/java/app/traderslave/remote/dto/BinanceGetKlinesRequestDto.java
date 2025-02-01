package app.traderslave.remote.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BinanceGetKlinesRequestDto {
    @NotNull(message = "Symbol is mandatory")
    @Size(min = 1, message = "Symbol must not be empty")
    private String symbol;
    @NotNull(message = "Interval is mandatory")
    private String interval;
    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 1000, message = "Limit must be at most 1500")
    private Integer limit;
    private Long startTime;
    private Long endTime;
}
