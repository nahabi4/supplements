package nahabi4.supplement.java.sql;

import java.sql.ResultSetMetaData;

public class ColumnMetadata {
    private String columnName;
    private String columnLabel;
    private int columnType;
    private String columnTypeName;
    private int precision;
    private int scale;
    private int columnDisplaySize;
    private int nullableFlag;
    private boolean autoIncrement;
    private boolean caseSensitive;
    private boolean searchable;
    private boolean currency;
    private boolean signed;
    private boolean readOnly;
    private boolean writable;
    private boolean definitelyWritable;

    private ColumnMetadata() {
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnLabel() {
        return columnLabel;
    }

    public int getColumnType() {
        return columnType;
    }

    public String getColumnTypeName() {
        return columnTypeName;
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

    public int getColumnDisplaySize() {
        return columnDisplaySize;
    }

    public boolean isNullable() {
        return nullableFlag == ResultSetMetaData.columnNullable;
    }

    public int getNullableFlag() {
        return nullableFlag;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public boolean isCurrency() {
        return currency;
    }

    public boolean isSigned() {
        return signed;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isWritable() {
        return writable;
    }

    public boolean isDefinitelyWritable() {
        return definitelyWritable;
    }


    public static class Builder {
        private ColumnMetadata constructed;

        public Builder() {
            this.constructed = new ColumnMetadata();
        }

        public Builder(ColumnMetadata columnMetadata) {
            this();
            
            this.constructed.columnName = columnMetadata.columnName;
            this.constructed.columnLabel = columnMetadata.columnLabel;
            this.constructed.columnType = columnMetadata.columnType;
            this.constructed.columnTypeName = columnMetadata.columnTypeName;
            this.constructed.precision = columnMetadata.precision;
            this.constructed.scale = columnMetadata.scale;
            this.constructed.columnDisplaySize = columnMetadata.columnDisplaySize;
            this.constructed.nullableFlag = columnMetadata.nullableFlag;
            this.constructed.autoIncrement = columnMetadata.autoIncrement;
            this.constructed.caseSensitive = columnMetadata.caseSensitive;
            this.constructed.searchable = columnMetadata.searchable;
            this.constructed.currency = columnMetadata.currency;
            this.constructed.signed = columnMetadata.signed;
            this.constructed.readOnly = columnMetadata.readOnly;
            this.constructed.writable = columnMetadata.writable;
            this.constructed.definitelyWritable = columnMetadata.definitelyWritable;

        }

        public Builder setColumnName(String columnName) {
            constructed.columnName = columnName;
            return this;
        }

        public Builder setColumnLabel(String columnLabel) {
            constructed.columnLabel = columnLabel;
            return this;
        }

        public Builder setColumnType(int columnType) {
            constructed.columnType = columnType;
            return this;
        }

        public Builder setColumnTypeName(String columnTypeName) {
            constructed.columnTypeName = columnTypeName;
            return this;
        }

        public Builder setPrecision(int precision) {
            constructed.precision = precision;
            return this;
        }

        public Builder setScale(int scale) {
            constructed.scale = scale;
            return this;
        }

        public Builder setColumnDisplaySize(int columnDisplaySize) {
            constructed.columnDisplaySize = columnDisplaySize;
            return this;
        }

        public Builder setNullable(boolean nullable) {
            constructed.nullableFlag = nullable ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
            return this;
        }        

        public Builder setNullableFlag(int nullableFlag) {
            constructed.nullableFlag = nullableFlag;
            return this;
        }

        public Builder setAutoIncrement(boolean autoIncrement) {
            constructed.autoIncrement = autoIncrement;
            return this;
        }

        public Builder setCaseSensitive(boolean caseSensitive) {
            constructed.caseSensitive = caseSensitive;
            return this;
        }

        public Builder setSearchable(boolean searchable) {
            constructed.searchable = searchable;
            return this;
        }

        public Builder setCurrency(boolean currency) {
            constructed.currency = currency;
            return this;
        }

        public Builder setSigned(boolean signed) {
            constructed.signed = signed;
            return this;
        }

        public Builder setReadOnly(boolean readOnly) {
            constructed.readOnly = readOnly;
            return this;
        }

        public Builder setWritable(boolean writable) {
            constructed.writable = writable;
            return this;
        }

        public Builder setDefinitelyWritable(boolean definitelyWritable) {
            constructed.definitelyWritable = definitelyWritable;
            return this;
        }

        public ColumnMetadata build() {
            return constructed;
        }
    }
}

