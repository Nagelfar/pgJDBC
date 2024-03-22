package pgJDBC.java;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

public class Sql {
    private Sql() {
    }

    public static Parameter.Named parameter(String name, Value value) {
        return new Parameter.Named(name, value);
    }

    public static SqlBuilder from(Connection connection) {
        return new SqlBuilder(
                new ConnectionConfiguration(connection, TypeMap.defaultTypeMap),
                null,
                new SqlBuilder.Parameters.NoParameters()
        );
    }

    public interface Parameter {
        Value value();

        record Named(String name, Value value) implements Parameter {
            public Named {
                Objects.requireNonNull(name);
                Objects.requireNonNull(value);
                if (name.isEmpty())
                    throw new IllegalArgumentException("parameter name must not be empty");
                if (!name.startsWith(":"))
                    throw new IllegalArgumentException("parameter names must start with : - but got " + name);
            }
        }

        record Positional(int position, Value value) implements Parameter{
            public Positional {
                Objects.requireNonNull(value);
                if (position < 0)
                    throw new IllegalArgumentException("Expecting a valid position");
            }
        }
    }


    public sealed interface Value {

        record Null(Class<?> type) implements Value {
        }

        record SqlInt(int value) implements Value {
        }

        record SqlString(String value) implements Value {
        }

        record SqlUUID(UUID value) implements Value {
        }

        static <T> Value ofNull(Class<T> type) {
            return new Null(type);
        }

        static Value of(int value) {
            return new SqlInt(value);
        }

        static Value of(Integer value) {
            return map(value, SqlInt::new, Integer.class);
        }

        static Value of(String value) {
            return map(value, SqlString::new, String.class);
        }

        static Value of(UUID value) {
            return map(value, SqlUUID::new, UUID.class);
        }

        private static <T> Value map(T value, Function<T, Value> mapper, Class<T> type) {
            return Optional.ofNullable(value).map(mapper).orElse(ofNull(type));
        }
    }
}
