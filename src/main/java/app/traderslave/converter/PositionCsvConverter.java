package app.traderslave.converter;

import app.traderslave.controller.dto.JupiterPerpetualCsvReqDto.*;
import com.opencsv.bean.AbstractBeanField;

public class PositionCsvConverter extends AbstractBeanField<Position, String> {

    @Override
    protected Position convert(String value) {
        return Position.valueOf(value.trim().toUpperCase());
    }

}