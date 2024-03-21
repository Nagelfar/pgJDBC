package pgJDBC.java;

import org.intellij.lang.annotations.Language;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

public class Sql {
    private Sql() {
    }

    public static Parameter.NamedParameter parameter(String name, ParameterValue value) {
        return new Parameter.NamedParameter(name, value);
    }

    public static SqlBuilder from(Connection connection) {
        return new SqlBuilder(
                connection,
                null,
                null
        );
    }

    public sealed interface Parameter {
        record NamedParameter(String name, ParameterValue value) {
            public NamedParameter {
                Objects.requireNonNull(name);
                Objects.requireNonNull(value);
            }
        }

        record NamedParameters(List<NamedParameter> parameters) implements Parameter {
        }

        record PositionalParameters(List<ParameterValue> parameters) implements Parameter {
        }
    }

    public sealed interface ParameterValue {
        record Null(int sqlType) implements ParameterValue {
        }

        record Int(int value) implements ParameterValue {
        }

        static ParameterValue of(int value) {
            return new Int(value);
        }

        static ParameterValue of(Integer value) {
            return map(value, Int::new, Types.INTEGER);
        }

        private static <T> ParameterValue map(T value, Function<T, ParameterValue> mapper, int sqlType) {
            return Optional.ofNullable(value).map(mapper).orElse(new Null(sqlType));
        }
    }
}
