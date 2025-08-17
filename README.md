# Trader-slave

L'applicativo è un sistema per la simulazione e gestione di operazioni di trading su coppie di valute (es. BTC/USDT). 
E' progettato per simulare scenari di trading realistici, fornendo strumenti per analisi e test di strategie.

Di seguito una sintesi delle sue funzionalità principali:

### **Caratteristiche principali**

1. **Esecuzione di simulazioni di trading**:
   - Creazione di simulazioni con parametri personalizzati (es. coppia di valute, descrizione, orario di inizio).
   - Gestione degli ordini di trading all'interno di una simulazione (es. acquisto, vendita, leva finanziaria).
2. **Gestione degli ordini**:
   - Creazione di ordini di trading con specifiche come tipo di ordine (BUY/SELL), leva finanziaria, quantità e orario.
   - Chiusura degli ordini con calcolo di profitto/perdita, variazioni percentuali e durata dell'operazione.
3. **Chiusura delle simulazioni**:
   - Chiusura di tutte le operazioni aperte in una simulazione.
   - Generazione di un riepilogo con statistiche dettagliate (es. bilancio iniziale/finale, numero di ordini in profitto/perdita, durata totale della simulazione).
4. **Dataset di mercato**:
   - Recupero di dati storici di mercato (candlestick) per analisi e simulazioni.
   - Parametri configurabili come coppia di valute, timeframe e intervallo temporale.

### **Tecnologie utilizzate**

- **Java: version 17**
- **Database**: H2 utilizzato per la gestione dei dati in locale ma è già configurato anche il db mysql in un altro file di "properties"
- **Spring boot: version 3.4**
- **Other:** Optional, API stream, Web-flux, Hibernate, Crud-repository, Exception-handler, Design pattern......

 
### **API principali**
1. **DatasetController**:
   - Endpoint per ottenere dati storici di mercato (candlestick).

2. **SimulationController**:
   - Creazione e gestione delle simulazioni.
   - Endpoint per avviare, aggiornare e chiudere simulazioni.

3. **SimulationOrderController**:
   - Creazione e gestione degli ordini di trading all'interno di una simulazione.

### **Demo**:
   - Link https://pittalavito.github.io/trader-slave-demo/html/main.html
   - Repository: https://github.com/pittalavito/trader-slave-demo


### **Eseguibile**:
   - Per il jar compreso di collection postman e file di utility per l'esecuzione in locale -> https://github.com/pittalavito/trader-slave-release
