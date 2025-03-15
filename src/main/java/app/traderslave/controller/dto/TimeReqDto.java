package app.traderslave.controller.dto;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Data
@FieldNameConstants
public class TimeReqDto {
    private boolean realTimeRequest = false;
    private LocalDateTime startTime;
}
