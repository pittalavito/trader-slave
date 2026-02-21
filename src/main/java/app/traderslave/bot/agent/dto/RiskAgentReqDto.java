package app.traderslave.bot.agent.dto;

import app.traderslave.bot.BotConfig;

public record RiskAgentReqDto(
    MarketDataAgentDto marketData,
    PortfolioAgentDto portfolio,
    BotConfig config
) {

}

