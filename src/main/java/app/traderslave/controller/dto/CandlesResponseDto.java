package app.traderslave.controller.dto;

import lombok.Builder;
import lombok.Data;;
import java.util.List;

@Data
@Builder
public class CandlesResponseDto {
    private List<CandleResponseDto> list;
    private Integer size;
}
