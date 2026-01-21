package app.traderslave.model.domain;

import app.traderslave.utility.SqlColumnDefinition;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@Entity
@NoArgsConstructor
@Table(name = "BINANCE_CANDLE")
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BinanceCandle extends BasePersistentModel {

    @Lob
    @Column(nullable = false)
    private String candles;

    public List<Object[]> getCandles() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(candles, new TypeReference<List<Object[]>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert candlesMap to Map", e);
        }
    }

    public void setCandles(List<Object[]> candles) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.candles = objectMapper.writeValueAsString(candles);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert Map to candlesMap", e);
        }
    }
}
