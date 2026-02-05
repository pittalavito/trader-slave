package app.traderslave.converter;

import com.opencsv.bean.AbstractBeanField;

public class OrderTypeCsvConverter extends AbstractBeanField<OrderType, String> {

    @Override
    protected OrderType convert(String currency) {
        return OrderType.valueOf(currency.trim().toUpperCase());
    }

}
