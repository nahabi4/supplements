package nahabi4.supplement.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import nahabi4.supplement.java.sql.ColumnMetadata;

public class StringArraysBulkRecordSet extends ArraysBulkRecordSet {

    private StringToObjectTransformer transformer;

    public StringArraysBulkRecordSet(ColumnMetadata[] columnMetadata, String[][] data, StringToObjectTransformer transformer) {
        super(columnMetadata, data);
        this.transformer = transformer;
    }

    @Override
    public Object[] getRowData() throws SQLServerException {
        Object[] sourceData = super.getRowData();
        Object[] resultData = new String[sourceData.length];

        for(int i=0; i < sourceData.length; i++){
            resultData[i] = transformer.transform((String) sourceData[i], columnMetadata[i]);
        }

        return resultData;
    }

}
