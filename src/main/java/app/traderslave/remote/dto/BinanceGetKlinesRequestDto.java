package app.traderslave.remote.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BinanceGetKlinesRequestDto {
    private String symbol;
    private String interval;
    private int limit;
}
