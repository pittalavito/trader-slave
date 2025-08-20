package app.traderslave.converter;

import app.traderslave.controller.dto.JupiterPerpetualCsvReqDto.*;
import com.opencsv.bean.AbstractBeanField;

public class OrderTypeCsvConverter extends AbstractBeanField<OrderType, String> {

    @Override
    protected OrderType convert(String currency) {
        return OrderType.valueOf(currency.trim().toUpperCase());
    }

}
