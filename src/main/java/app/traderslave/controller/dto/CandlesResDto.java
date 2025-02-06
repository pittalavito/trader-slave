package app.traderslave.controller.dto;

import lombok.Builder;
import lombok.Data;;
import java.util.List;

@Data
@Builder
public class CandlesResDto {
    private List<CandleResDto> list;
    private Integer size;
}
