package app.traderslave.converter;

import app.traderslave.model.domain.ReportOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;

@Converter
public class ReportOrderConverter implements AttributeConverter<ReportOrder, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SneakyThrows
    public ReportOrder convertToEntityAttribute(String string) {
        return objectMapper.readValue(string, ReportOrder.class);
    }

    @Override
    @SneakyThrows
    public String convertToDatabaseColumn(ReportOrder report) {
        return objectMapper.writeValueAsString(report);
    }
}