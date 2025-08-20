package app.traderslave.converter;

import app.traderslave.controller.dto.JupiterPerpetualCsvReqDto.*;
import com.opencsv.bean.AbstractBeanField;

public class PositionChangeCsvConverter extends AbstractBeanField<PositionChange, String> {

    @Override
    protected PositionChange convert(String value) {
        return PositionChange.valueOf(value.trim().toUpperCase());
    }

}