package app.traderslave.converter;

import com.opencsv.bean.AbstractBeanField;

import java.math.BigDecimal;

public class BigDecimalCsvConverter extends AbstractBeanField<BigDecimal, String> {

    @Override
    protected BigDecimal convert(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(value.trim()).setScale(2);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid BigDecimal value: " + value, e);
        }
    }

}
