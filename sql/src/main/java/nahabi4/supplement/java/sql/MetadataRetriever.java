package nahabi4.supplement.java.sql;

import java.sql.*;

public class MetadataRetriever {

    public static ColumnMetadata[] getTableColumnsMetadata(String tableName, Connection connection) throws SQLException {
        ColumnMetadata[] columnsMetadata;

        final String sql = "SELECT * FROM " + tableName + " WHERE 1=0 ";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            try (ResultSet rs = stmt.getResultSet()) {
                ResultSetMetaData rsMd = rs.getMetaData();

                int columnsCount = rsMd.getColumnCount();
                columnsMetadata = new ColumnMetadata[columnsCount];
                for (int i = 1; i <= columnsCount; i++) {
                    columnsMetadata[i] = new ColumnMetadata.Builder()
                            .setColumnLabel(rsMd.getColumnLabel(i))
                            .setColumnName(rsMd.getColumnName(i))
                            .setAutoIncrement(rsMd.isAutoIncrement(i))
                            .setCaseSensitive(rsMd.isCaseSensitive(i))
                            .setNullableFlag(rsMd.isNullable(i))
                            .setColumnDisplaySize(rsMd.getColumnDisplaySize(i))
                            .setPrecision(rsMd.getPrecision(i))
                            .setScale(rsMd.getScale(i))
                            .setColumnType(rsMd.getColumnType(i))
                            .setColumnTypeName(rsMd.getColumnTypeName(i))
                            .setCurrency(rsMd.isCurrency(i))
                            .setSearchable(rsMd.isSearchable(i))
                            .setReadOnly(rsMd.isReadOnly(i))
                            .setWritable(rsMd.isWritable(i))
                            .setDefinitelyWritable(rsMd.isDefinitelyWritable(i))
                            .build();
                }
            }
        }

        return columnsMetadata;
    }
}
