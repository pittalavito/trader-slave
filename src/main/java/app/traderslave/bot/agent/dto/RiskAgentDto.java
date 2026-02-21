package app.traderslave.bot.agent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskAgentDto {
    private Boolean canOpenOrder;
    private RiskLevel riskLevel;

    public boolean canOpenOrder() {
        return Boolean.TRUE == canOpenOrder;
    }

    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        VERY_LOW,
        VERY_HIGH,
        EXTREME
    }
}
