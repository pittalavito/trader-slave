package app.traderslave.converter;

import com.opencsv.bean.AbstractBeanField;
import app.traderslave.model.dto.req.JupiterPerpetualCsvReqDto.Position;

public class PositionCsvConverter extends AbstractBeanField<Position, String> {

    @Override
    protected Position convert(String value) {
        return Position.valueOf(value.trim().toUpperCase());
    }

}