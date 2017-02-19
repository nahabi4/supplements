package nahabi4.supplement.sqlserver.jdbc;


import com.microsoft.sqlserver.jdbc.SQLServerException;
import nahabi4.supplement.java.sql.ColumnMetadata;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import static java.sql.Types.INTEGER;

public class BulkLoadDataPreparator implements StringToObjectTransformer {

    /* 
    almost copy-paste from com.microsoft.sqlserver.jdbc.SQLServerBulkCSVFileRecord#addColumnMetadataInternal 
     */
    public static ColumnMetadata transformMetadata(ColumnMetadata metadata) {
        ColumnMetadata.Builder metadataBuilder = new ColumnMetadata.Builder(metadata);

        switch (metadata.getColumnType()) {
             /*
             * SQL Server supports numerous string literal formats for temporal types, hence sending them as varchar with approximate
             * precision(length) needed to send supported string literals. string literal formats supported by temporal types are available in MSDN
             * page on data types.
             */
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
            case microsoft.sql.Types.DATETIMEOFFSET:
                // The precision is just a number long enough to hold all types of temporal data, doesn't need to be exact precision.
                metadataBuilder.setPrecision(50);
                break;

            // Redirect SQLXML as LONGNVARCHAR
            // SQLXML is not valid type in TDS
            case Types.SQLXML:
                metadataBuilder.setColumnType(Types.LONGNVARCHAR);
                break;

            // Redirecting Float as Double based on data type mapping
            // https://msdn.microsoft.com/en-us/library/ms378878%28v=sql.110%29.aspx
            case Types.FLOAT:
                metadataBuilder.setColumnType(Types.DOUBLE);
                break;

            // redirecting BOOLEAN as BIT
            case Types.BOOLEAN:
                metadataBuilder.setColumnType(Types.BIT);
                break;
        }

        return metadataBuilder.build();
    }

    /*
    almost copy-paste from com.microsoft.sqlserver.jdbc.SQLServerBulkCSVFileRecord.getRowData
     */
    @Override
    public Object transform(String value, ColumnMetadata metadata) {
        Object result;
        ColumnMetadata preparedMetadata = transformMetadata(metadata);

        switch (preparedMetadata.getColumnType()) {
            /*
             * Both BCP and BULK INSERT considers double quotes as part of the data and throws error if any data (say "10") is to be
             * inserted into an numeric column. Our implementation does the same.
             */
            case INTEGER: {
                // Formatter to remove the decimal part as SQL Server floors the decimal in integer types
                DecimalFormat decimalFormatter = new DecimalFormat("#");
                String formattedInput = decimalFormatter.format(Double.parseDouble(value));
                result = Integer.valueOf(formattedInput);
                break;
            }

            case Types.TINYINT:
            case Types.SMALLINT: {
                // Formatter to remove the decimal part as SQL Server floors the decimal in integer types
                DecimalFormat decimalFormatter = new DecimalFormat("#");
                String formattedInput = decimalFormatter.format(Double.parseDouble(value));
                result = Short.valueOf(formattedInput);
                break;
            }

            case Types.BIGINT: {
                BigDecimal bd = new BigDecimal(value.trim());
                try {
                    result = bd.setScale(0, BigDecimal.ROUND_DOWN).longValueExact();
                } catch (ArithmeticException ex) {
                    String msgValue = "'" + value + "'";
                    String msgType = metadata.getColumnTypeName();
                    MessageFormat form = new MessageFormat("An error occurred while converting the {0} value to JDBC data type {1}.");
                    String reason = form.format(new Object[] {msgValue, msgType});
                    throw new RuntimeException(reason, ex);
                }
                break;
            }

            case Types.DECIMAL:
            case Types.NUMERIC: {
                BigDecimal bd = new BigDecimal(value.trim());
                result = bd.setScale(preparedMetadata.getScale(), RoundingMode.HALF_UP);
                break;
            }

            case Types.BIT: {
                // "true" => 1, "false" => 0
                // Any non-zero value (integer/double) => 1, 0/0.0 => 0
                try {
                    result = (0 == Double.parseDouble(value)) ? Boolean.FALSE : Boolean.TRUE;
                } catch (NumberFormatException e) {
                    result = Boolean.parseBoolean(value);
                }
                break;
            }

            case Types.REAL: {
                result = Float.parseFloat(value);
                break;
            }

            case Types.DOUBLE: {
                result = Double.parseDouble(value);
                break;
            }

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB: {
                /*
                 * For binary data, the value in file may or may not have the '0x' prefix. We will try to match our implementation with
                 * 'BULK INSERT' except that we will allow 0x prefix whereas 'BULK INSERT' command does not allow 0x prefix. A BULK INSERT
                 * example: A sample csv file containing data for 2 binary columns and 1 row: 61,62 Table definition: create table t1(c1
                 * varbinary(10), c2 varbinary(10)) BULK INSERT command: bulk insert t1 from 'C:\in.csv'
                 * with(DATAFILETYPE='char',firstrow=1,FIELDTERMINATOR=',') select * from t1 shows 1 row with columns: 0x61, 0x62
                 */
                // Strip off 0x if present.
                String binData = value.trim();
                if (binData.startsWith("0x") || binData.startsWith("0X")) {
                    result = binData.substring(2);
                } else {
                    result = binData;
                }
                break;
            }

            case Types.NULL: {
                result = null;
                break;
            }

            case Types.DATE:
            case Types.CHAR:
            case Types.NCHAR:
            case Types.VARCHAR:
            case Types.NVARCHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.CLOB:
            default: {
                // The string is copied as is.
                result = value;
                break;
            }
        }

        return result;
    }
}
