package nahabi4.supplement.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ISQLServerBulkRecord;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import nahabi4.supplement.java.sql.ColumnMetadata;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ArraysBulkRecordSet implements ISQLServerBulkRecord {

    protected final ColumnMetadata[] columnMetadata;
    private final Object[][] data;

    private int currentPosition = -1;

    private final Set<Integer> ordinals;

    public ArraysBulkRecordSet(Object[][] data, ColumnMetadata[] columnMetadata) {
        this.columnMetadata = convertToBulkCopySupportedTypes(columnMetadata);
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

    private ColumnMetadata[] convertToBulkCopySupportedTypes(ColumnMetadata[] columnMetadata) {
        ColumnMetadata[] convertedMetadata = new ColumnMetadata[columnMetadata.length];
        for(int i=0; i< columnMetadata.length; i++){
            convertedMetadata[i] = convertToBulkCopySupportedTypes(columnMetadata[i]);
        }
        return convertedMetadata;
    }

    /*
    almost copy-paste from com.microsoft.sqlserver.jdbc.SQLServerBulkCSVFileRecord#addColumnMetadataInternal
     */
    private ColumnMetadata convertToBulkCopySupportedTypes(ColumnMetadata metadata) {
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
}
