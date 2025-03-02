package app.traderslave.controller.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeReqDto {
    private boolean realTimeRequest = false;
    private LocalDateTime time;
}
