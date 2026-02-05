package app.traderslave.model.dto.req;

import app.traderslave.model.enums.CurrencyPair;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class CandleReqDto extends TimeReqDto {

    @NotNull(message = "required")
    private CurrencyPair currencyPair;

}
