package pgJDBC.java;

import org.intellij.lang.annotations.Language;
import pgJDBC.java.statements.SqlPreparedStatement;
import pgJDBC.java.statements.SqlStatement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public record SqlBuilder(
        ConnectionConfiguration connection,
        String queryString,
        Parameters parameters
) implements AutoCloseable {
    // language=SQL

    /**
     * @param sql
     * @return
     */
    public SqlBuilder query(@Language("SQL") String sql) {
        return new SqlBuilder(connection, sql, parameters);
    }

    public SqlBuilder parameters(Sql.Parameter.Named... parameters) {
        return new SqlBuilder(
                connection,
                queryString,
                Parameters.Named.fromArray(parameters)
        );
    }

    public SqlBuilder parameters(Sql.Value... parameters) {
        return new SqlBuilder(
                connection,
                queryString,
                Parameters.Positional.fromArray(parameters)
        );
    }

    public int executeNonQuery() {
        try {
            try (var statement = buildStatement(this)) {
                return statement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Collection<T> execute(Function<RowReader, T> map) {
        try {
            try (var statement = buildStatement(this)) {
                var resultSet = statement.executeQuery();
                var results = new ArrayList<T>();
                var rowReader = new RowReader(resultSet);
                while (resultSet.next()) {
                    results.add(map.apply(rowReader));
                }
                return results;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        connection.connection().close();
    }

    static SqlStatement buildStatement(SqlBuilder builder) throws SQLException {
        return switch (builder.parameters()) {
            case SqlBuilder.Parameters.Named(var named) -> applyParameters(
                    SqlPreparedStatement.named(builder.connection(), builder.queryString()),
                    named
            );
            case SqlBuilder.Parameters.Positional(var positional) -> applyParameters(
                    SqlPreparedStatement.positional(builder.connection(), builder.queryString()),
                    positional)
            ;
            case SqlBuilder.Parameters.NoParameters ignored ->
                    SqlStatement.noParameters(builder.connection(), builder.queryString());
        };
    }

    static <T extends Sql.Parameter> SqlPreparedStatement<T> applyParameters(SqlPreparedStatement<T> statement, List<T> parameters) throws SQLException {
        for (var parameter : parameters)
            statement.setValue(parameter);
        return statement;
    }

    sealed interface Parameters {

        record NoParameters() implements Parameters {
        }

        record Named(List<Sql.Parameter.Named> parameters) implements Parameters {
            public static Named fromArray(Sql.Parameter.Named[] parameters) {
                return new Named(List.of(parameters));
            }
        }

        record Positional(List<Sql.Parameter.Positional> parameters) implements Parameters {
            public static Positional fromArray(Sql.Value[] parameters) {
                return new Parameters.Positional(
                        IntStream.range(0, parameters.length)
                                .mapToObj(index -> new Sql.Parameter.Positional(index, parameters[index]))
                                .toList()
                );
            }
        }
    }
}
