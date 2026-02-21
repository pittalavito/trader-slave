package app.traderslave.bot.agent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignalAgentDto {

    private Boolean noSignal;
}
