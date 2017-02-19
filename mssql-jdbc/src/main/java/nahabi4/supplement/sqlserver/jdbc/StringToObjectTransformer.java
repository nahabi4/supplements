package nahabi4.supplement.sqlserver.jdbc;

import nahabi4.supplement.java.sql.ColumnMetadata;

import java.text.ParseException;

public interface StringToObjectTransformer {

    Object transform(String value, ColumnMetadata metadata);
}
