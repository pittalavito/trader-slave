package app.traderslave.converter;

import com.opencsv.bean.AbstractBeanField;
import app.traderslave.model.dto.req.JupiterPerpetualCsvReqDto.PositionChange;

public class PositionChangeCsvConverter extends AbstractBeanField<PositionChange, String> {

    @Override
    protected PositionChange convert(String value) {
        return PositionChange.valueOf(value.trim().toUpperCase());
    }

}