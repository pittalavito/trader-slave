package app.traderslave.controller.dto;

import app.traderslave.converter.*;
import app.traderslave.model.enums.Currency;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class JupiterPerpetualCsvReqDto {

    @CsvBindByName(column = "Created at")
    private String createdAt;

    @CsvBindByName(column = "Transaction ID")
    private String transactionId;

    @CsvCustomBindByName(column = "Asset", converter = CurrencyCsvConverter.class)
    private Currency asset;

    @CsvCustomBindByName(column = "Position", converter = PositionCsvConverter.class)
    private Position position;

    @CsvCustomBindByName(column = "Position change", converter = PositionChangeCsvConverter.class)
    private PositionChange positionChange;

    @CsvCustomBindByName(column = "Order type", converter = OrderTypeCsvConverter.class)
    private OrderType orderType;

    @CsvCustomBindByName(column = "Deposit / Withdraw ($)", converter = BigDecimalCsvConverter.class)
    private BigDecimal depositWithdraw;

    @CsvCustomBindByName(column = "Execution price ($)", converter = BigDecimalCsvConverter.class)
    private BigDecimal executionPrice;

    @CsvCustomBindByName(column = "Trade size ($)", converter = BigDecimalCsvConverter.class)
    private BigDecimal tradeSize;

    @CsvCustomBindByName(column = "Profit / Loss ($)", converter = BigDecimalCsvConverter.class)
    private BigDecimal profitLoss;

    @CsvCustomBindByName(column = "Trade fee ($)", converter = BigDecimalCsvConverter.class)
    private BigDecimal tradeFee;

    @CsvCustomBindByName(column = "Liquidation fee ($)", converter = BigDecimalCsvConverter.class)
    private BigDecimal liquidationFee;

    @CsvCustomBindByName(column = "Mint",  converter = CurrencyCsvConverter.class)
    private Currency mint;

    @CsvCustomBindByName(column = "Collateral", converter = CurrencyCsvConverter.class)
    private Currency collateral;

    @CsvCustomBindByName(column = "Collateral mint", converter = CurrencyCsvConverter.class)
    private Currency collateralMint;

    public boolean isIncrease() {
        return positionChange == PositionChange.INCREASE;
    }

    public boolean isDecrease() {
        return positionChange == PositionChange.DECREASE;
    }

    public boolean isProfit() {
        return profitLoss.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isLoss() {
        return profitLoss.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isLiquidation() {
        return orderType == OrderType.LIQUIDATION;
    }

    public enum Position {LONG, SHORT, NONE}
    public enum PositionChange {DECREASE, INCREASE, NONE}
    public enum OrderType {MARKET, LIQUIDATION, TRIGGER}
}