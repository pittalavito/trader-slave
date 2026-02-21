package app.traderslave.bot.agent.impl.risk;

import app.traderslave.bot.agent.dto.RiskAgentDto;
import app.traderslave.bot.agent.dto.RiskAgentReqDto;

public abstract class BaseRiskAgent {

    public abstract RiskAgentDto calculate(RiskAgentReqDto request);
}
