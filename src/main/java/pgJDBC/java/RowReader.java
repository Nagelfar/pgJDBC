package pgJDBC.java;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class RowReader {

    private final ResultSet resultSet;

    public RowReader(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    private <T> T getFieldValue(String columnName, Class<T> tClass) {
        try {
            var result = resultSet.getObject(columnName, tClass);
            if (result == null || resultSet.wasNull())
                throw new UnsupportedOperationException("Expected non-null value for " + columnName);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Optional<T> getOptionalFieldValue(String columnName, Class<T> tClass) {
        try {
            return Optional.ofNullable(resultSet.getObject(columnName, tClass));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String string(String columnName) {
        return getFieldValue(columnName, String.class);
    }

    public Optional<String> stringOptional(String columnName) {
        return getOptionalFieldValue(columnName, String.class);
    }

    public int integer(String columnName) {
        return getFieldValue(columnName, Integer.class);
    }

    public Optional<Integer> integerOptional(String columnName) {
        return getOptionalFieldValue(columnName, Integer.class);
    }

    public UUID uuid(String columnName) {
        return getFieldValue(columnName, UUID.class);
    }

    public Optional<UUID> uuidOptional(String columnName) {
        return getOptionalFieldValue(columnName, UUID.class);
    }
}
