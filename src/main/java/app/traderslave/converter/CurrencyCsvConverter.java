package app.traderslave.converter;

import app.traderslave.model.enums.Currency;
import com.opencsv.bean.AbstractBeanField;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CurrencyCsvConverter extends AbstractBeanField<Currency, String> {

    @Override
    protected Currency convert(String value) {
        if (value.trim().isEmpty()) {
            log.warn("Received empty value for Currency conversion");
            return null;
        }

        try {

            return Currency.valueOf(value.trim().toUpperCase());

        } catch (IllegalArgumentException e) {
            log.warn("Failed to convert value '{}' to Currency enum: {}", value, e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

}
