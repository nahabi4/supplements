package nahabi4.supplement.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ISQLServerBulkRecord;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;
import nahabi4.supplement.java.sql.ColumnMetadata;
import nahabi4.supplement.java.sql.MetadataRetriever;

import java.sql.DriverManager;
import java.sql.SQLException;

public class BulkCopyHelper {

    public static void loadDataToTable(String connString, String table, String[] columnNames, String[][] dataRows) throws SQLException {
        ColumnMetadata[] columnMetadata = MetadataRetriever.getTableColumnsMetadata(table, DriverManager.getConnection(connString));

        ISQLServerBulkRecord record = new StringArraysBulkRecordSet(dataRows, columnMetadata, new StringToObjectDefaultTransformer());

        bulkCopy(connString, table, record, columnNames);
    }

    public static void bulkCopy(String connString, String table, ISQLServerBulkRecord record, String[] columnNames) throws SQLException {
        BulkRecordLoader loader = new BulkRecordLoader(connString);

        SQLServerBulkCopyOptions bulkOptions = loader.getBulkCopyOptions();
        bulkOptions.setKeepIdentity(true);
        bulkOptions.setKeepNulls(true);
        bulkOptions.setCheckConstraints(true);

        loader.load(table, record, columnNames);
        loader.closeConnection();
    }
}
