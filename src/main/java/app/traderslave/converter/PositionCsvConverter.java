package app.traderslave.converter;

import com.opencsv.bean.AbstractBeanField;

public class PositionCsvConverter extends AbstractBeanField<Position, String> {

    @Override
    protected Position convert(String value) {
        return Position.valueOf(value.trim().toUpperCase());
    }

}