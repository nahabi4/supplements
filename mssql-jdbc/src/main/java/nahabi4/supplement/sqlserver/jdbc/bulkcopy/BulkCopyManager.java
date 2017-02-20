package nahabi4.supplement.sqlserver.jdbc.bulkcopy;

import com.microsoft.sqlserver.jdbc.ISQLServerBulkRecord;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;
import java.sql.SQLException;

public class BulkCopyManager {

    private SQLServerBulkCopy bulkCopy;

    public BulkCopyManager(String connString) throws SQLException {
        bulkCopy = new SQLServerBulkCopy(connString);
    }

    public BulkCopyManager(Connection connection) throws SQLException {
        bulkCopy = new SQLServerBulkCopy(connection);
    }

    public SQLServerBulkCopyOptions getBulkCopyOptions() {
        return bulkCopy.getBulkCopyOptions();
    }

    public void closeConnection(){
        bulkCopy.close();
    }

    public void load(String table, ISQLServerBulkRecord record, String[] destColumnNames) throws SQLServerException {
        bulkCopy.setDestinationTableName(table);
        setColumnMapping(destColumnNames);
        bulkCopy.writeToServer(record);
    }

    private void setColumnMapping(String[] columnNames) throws SQLServerException {
        bulkCopy.clearColumnMappings();
        for(int i=0; i < columnNames.length; i++){
            bulkCopy.addColumnMapping(i+1, columnNames[i]);
        }
    }

}
