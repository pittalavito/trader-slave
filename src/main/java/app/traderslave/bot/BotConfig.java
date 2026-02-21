package app.traderslave.bot;

import app.traderslave.model.enums.CurrencyPair;
import app.traderslave.model.enums.TimeFrame;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BotConfig {

    private String botName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CurrencyPair currencyPair;
    private BotParameters botParameters;
    private TimeFrame timeFrameAnalysis;

    @Data
    public static class BotParameters {
        // Capital
        private double initialCapital;
        private double maxLeverage;
        private double maxDrawDown;
        // Trading frequency
        private int barIntervalInMinutes;
        private int maxTradesPerDay;
        // Risk
        private double riskPerTrade;
        private double maxPositionSizeInUsd;
        // Alpha thresholds
        private double trendThreshold;
        private double meanReversionThreshold;
        // MPC / Control (future)
        private int mpcHorizon;
        private double mpcLambda;
    }
}

// === Market Data ===
//private MarketDataService marketDataService;

// === Alpha Models ===
//private AlphaModel alphaModel; // segnali di trading // quando entrare

// === Portfolio & Risk ===
//private Portfolio portfolio;
//private PositionSizer positionSizer; // quanto entrare
//private RiskManager riskManager; // quando uscire per sicurezza

// === Trading Engine ===
//private TradingEngine tradingEngine;
//private OrderExecutor orderExecutor;

// === Backtesting / Simulation ===
//private BacktestEngine backtestEngine;

// === General Config ===
