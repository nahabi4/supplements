package nahabi4.supplement.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import nahabi4.supplement.java.sql.ColumnMetadata;

public class StringArraysBulkRecordSet extends ArraysBulkRecordSet {

    protected StringToObjectTransformer transformer;

    public StringArraysBulkRecordSet(String[][] data, ColumnMetadata[] columnMetadata, StringToObjectTransformer transformer) {
        super(data, columnMetadata);
        this.transformer = transformer;
    }

    @Override
    public Object[] getRowData() throws SQLServerException {
        String[] sourceData = (String[]) super.getRowData();
        Object[] resultData = new Object[sourceData.length];

        for(int i=0; i < sourceData.length; i++){
            resultData[i] = transformer.transform(sourceData[i], columnMetadata[i]);
        }

        return resultData;
    }

}
