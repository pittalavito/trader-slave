package app.traderslave.bot;


public interface Bot {

    Object getMarketData();

    Object searchSignals();

    Object checkOrderConstraints();

    Object placeOrders();

    Object manageOpenOrders();

    Object logActivities();

    Object evaluatePerformance();

    Object adaptStrategy();

    Object shutdown();

}
