package app.traderslave.bot.engine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseTradingEngine {

    private boolean isRunning = false;

    public void start() {
        if (!isRunning) {
            isRunning = true;
            execute();
        } else {
            log.warn("BackTestEngine is already running.");
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
        } else {
            log.warn("BackTestEngine is not running.");
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    protected abstract void whileExecuting();

    private void execute() {
        while (isRunning) {
            try {
                whileExecuting();
            } catch (Exception e) {
                log.error("Error in trading engine execution", e);
            }
        }
    }

}
