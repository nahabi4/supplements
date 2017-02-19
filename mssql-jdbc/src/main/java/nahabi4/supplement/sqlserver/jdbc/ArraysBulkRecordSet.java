package nahabi4.supplement.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ISQLServerBulkRecord;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import nahabi4.supplement.java.sql.ColumnMetadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ArraysBulkRecordSet implements ISQLServerBulkRecord {

    protected final ColumnMetadata[] columnMetadata;
    private final Object[][] data;

    private int currentPosition = -1;

    private final Set<Integer> ordinals;

    public ArraysBulkRecordSet(ColumnMetadata[] columnMetadata, Object[][] data) {
        this.columnMetadata = columnMetadata;
        this.data = data;
        this.ordinals = generateOrdinals(columnMetadata.length);
    }

    private Set<Integer> generateOrdinals(int size) {
        Integer[] ordinals = new Integer[size];

        for (int i = 0; i < size; i++) {
            ordinals[i] = i + 1;
        }

        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(ordinals)));
    }

    @Override
    public Set<Integer> getColumnOrdinals() {
        return ordinals;
    }

    @Override
    public String getColumnName(int ordinal) {
        return getColumnMetadataByOrdinal(ordinal).getColumnName();
    }

    @Override
    public int getColumnType(int ordinal) {
        return getColumnMetadataByOrdinal(ordinal).getColumnType();
    }

    @Override
    public int getPrecision(int ordinal) {
        return getColumnMetadataByOrdinal(ordinal).getPrecision();
    }

    @Override
    public int getScale(int ordinal) {
        return getColumnMetadataByOrdinal(ordinal).getScale();
    }

    @Override
    public boolean isAutoIncrement(int ordinal) {
        return getColumnMetadataByOrdinal(ordinal).isAutoIncrement();
    }

    private ColumnMetadata getColumnMetadataByOrdinal(int ordinal) {
        return columnMetadata[getIndexByOrdinal(ordinal)];
    }

    private int getIndexByOrdinal(int ordinal) {
        return ordinal - 1;
    }

    @Override
    public Object[] getRowData() throws SQLServerException {
        return data[currentPosition];
    }

    @Override
    public boolean next() throws SQLServerException {
        return ++currentPosition < columnMetadata.length;
    }
}
