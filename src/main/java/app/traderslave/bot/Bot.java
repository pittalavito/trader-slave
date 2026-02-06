package app.traderslave.bot;

/**
 * Interface representing a trading bot with various functionalities
 * for interacting with the market, managing orders, and adapting strategies.
 */
public interface Bot {

    /**
     * Retrieves market data such as prices, volumes, and trends.
     *
     * @return an object containing market data / un oggetto contenente i dati di mercato.
     */
    Object getMarketData();

    /**
     * Searches for trading signals based on predefined strategies or patterns.
     *
     * @return an object containing detected signals / un oggetto contenente i segnali rilevati.
     */
    Object searchSignals();

    /**
     * Checks if the constraints for placing or managing orders are satisfied.
     *
     * @return an object indicating the result of the constraint checks / un oggetto che indica il risultato dei controlli sui vincoli.
     */
    Object checkOrderConstraints();

    /**
     * Places new orders in the market based on signals and strategies.
     *
     * @return an object representing the result of the order placement / un oggetto che rappresenta il risultato del piazzamento dell'ordine.
     */
    Object placeOrders();

    /**
     * Manages open orders, such as modifying or closing them based on market conditions.
     *
     * @return an object representing the result of the management operation / un oggetto che rappresenta il risultato dell'operazione di gestione.
     */
    Object manageOpenOrders();

    /**
     * Logs the bot's activities, such as executed trades and strategy decisions.
     *
     * @return an object representing the logging result / un oggetto che rappresenta il risultato della registrazione.
     */
    Object logActivities();

    /**
     * Evaluates the bot's performance, such as profit, loss, and strategy effectiveness.
     *
     * @return an object containing the performance evaluation / un oggetto contenente la valutazione delle prestazioni.
     */
    Object evaluatePerformance();

    /**
     * Adapts the bot's strategy based on market conditions or performance analysis.
     *
     * @return an object representing the result of the strategy adaptation / un oggetto che rappresenta il risultato dell'adattamento della strategia.
     */
    Object adaptStrategy();

    /**
     * Shuts down the bot, performing any necessary cleanup or final operations.
     *
     * @return an object indicating the result of the shutdown process / un oggetto che indica il risultato del processo di arresto.
     */
    Object shutdown();
}
