package app.traderslave.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SqlColumnDefinition {

    public final String VARCHAR_UID = "varchar(64)";
    public final String VARCHAR_20 = "varchar(20)";
    public final String VARCHAR_255 = "varchar(255)";
    public final String BIG_DECIMAL_30_2 = "decimal(38,2)";
    public final String BIG_DECIMAL_30_2_DEFAULT_0 = "decimal(38,2) default 0";
    public final String INTEGER_DEFAULT_0 = "integer default 0";
    public final String TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP  = "timestamp default current_timestamp";

}