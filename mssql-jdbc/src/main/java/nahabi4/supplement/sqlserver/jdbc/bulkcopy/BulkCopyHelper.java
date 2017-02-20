package nahabi4.supplement.sqlserver.jdbc.bulkcopy;

import com.microsoft.sqlserver.jdbc.ISQLServerBulkRecord;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;
import nahabi4.supplement.java.sql.ColumnMetadata;
import nahabi4.supplement.java.sql.MetadataRetriever;
import nahabi4.supplement.sqlserver.jdbc.StringArraysBulkRecordSet;
import nahabi4.supplement.sqlserver.jdbc.StringToObjectDefaultTransformer;

import java.sql.DriverManager;
import java.sql.SQLException;

public class BulkCopyHelper {

    public static void loadDataToTable(String connString, String table, String[] columnNames, String[][] dataRows) throws SQLException {
        ColumnMetadata[] columnMetadata = MetadataRetriever.getTableColumnsMetadata(table, DriverManager.getConnection(connString));

        ISQLServerBulkRecord record = new StringArraysBulkRecordSet(
                dataRows,
                selectMetadataForColumns(columnNames, columnMetadata),
                new StringToObjectDefaultTransformer());

        bulkCopy(connString, table, record, columnNames);
    }

    public static ColumnMetadata[] selectMetadataForColumns(String[] columnNames, ColumnMetadata[] columnMetadata) {
        ColumnMetadata[] selectedMetadata = new ColumnMetadata[columnNames.length];
        main: for (int i = 0; i < columnMetadata.length; i++) {
            final String curColumnName = columnMetadata[i].getColumnName();
            for (int j = 0; j < columnNames.length; j++) {
                if(curColumnName == columnNames[j]){
                    selectedMetadata[j] = columnMetadata[i];
                    continue main;
                }
            }
        }
        return selectedMetadata;
    }

    public static void bulkCopy(String connString, String table, ISQLServerBulkRecord record, String[] columnNames) throws SQLException {
        BulkCopyManager bulkCopyManager = new BulkCopyManager(connString);

        SQLServerBulkCopyOptions bulkOptions = bulkCopyManager.getBulkCopyOptions();
        bulkOptions.setUseInternalTransaction(true);
        bulkOptions.setKeepIdentity(true);
        bulkOptions.setKeepNulls(true);
        bulkOptions.setCheckConstraints(true);

        bulkCopyManager.load(table, record, columnNames);
        bulkCopyManager.closeConnection();
    }
}
