package pgJDBC.java;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

public class Sql {
    private Sql() {
    }

    public static NamedParameter parameter(String name, Value value) {
        return new NamedParameter(name, value);
    }

    public static SqlBuilder from(Connection connection) {
        return new SqlBuilder(
                connection,
                null,
                null
        );
    }

    public record NamedParameter(String name, Value value) {
        public NamedParameter {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
            if (name.isEmpty())
                throw new IllegalArgumentException("parameter name must not be empty");
        }
    }

    public sealed interface Value {
        record Null(int sqlType) implements Value {
        }

        record Int(int value) implements Value {
        }

        static Value of(int value) {
            return new Int(value);
        }

        static Value of(Integer value) {
            return map(value, Int::new, Types.INTEGER);
        }

        private static <T> Value map(T value, Function<T, Value> mapper, int sqlType) {
            return Optional.ofNullable(value).map(mapper).orElse(new Null(sqlType));
        }
    }
}
