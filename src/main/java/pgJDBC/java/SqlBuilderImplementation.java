package pgJDBC.java;

import org.intellij.lang.annotations.Language;
import pgJDBC.java.statements.SqlPreparedStatement;
import pgJDBC.java.statements.SqlStatement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

record SqlBuilderImplementation(
        ConnectionConfiguration connection,
        String queryString,
        Parameters parameters
) implements AutoCloseable, SqlBuilder {
    // language=SQL

    /**
     * @param sql
     * @return
     */
    @Override
    public SqlBuilder query(@Language("SQL") String sql) {
        return new SqlBuilderImplementation(connection, sql, parameters);
    }

    @Override
    public SqlBuilder parameters(Sql.Parameter.Named... parameters) {
        return new SqlBuilderImplementation(
                connection,
                queryString,
                Parameters.Named.fromArray(parameters)
        );
    }

    @Override
    public SqlBuilder parameters(Sql.Value... parameters) {
        return new SqlBuilderImplementation(
                connection,
                queryString,
                Parameters.Positional.fromArray(parameters)
        );
    }

    @Override
    public int executeNonQuery() {
        try {
            try (var statement = buildStatement(this)) {
                return statement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Collection<T> execute(Function<RowReader, T> map) {
        try {
            try (var statement = buildStatement(this)) {
                try (var resultSet = statement.executeQuery()) {
                    var results = new ArrayList<T>();
                    var rowReader = new RowReader(resultSet);
                    while (resultSet.next()) {
                        results.add(map.apply(rowReader));
                    }
                    return results;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        connection.connection().close();
    }

    static SqlStatement buildStatement(SqlBuilderImplementation builder) throws SQLException {
        return switch (builder.parameters()) {
            case Parameters.Named(var named) -> applyParameters(
                    SqlPreparedStatement.named(builder.connection(), builder.queryString()),
                    named
            );
            case Parameters.Positional(var positional) -> applyParameters(
                    SqlPreparedStatement.positional(builder.connection(), builder.queryString()),
                    positional
            );
            case Parameters.NoParameters ignored ->
                    SqlStatement.noParameters(builder.connection(), builder.queryString());
        };
    }

    static <T extends Sql.Parameter> SqlPreparedStatement<T> applyParameters(SqlPreparedStatement<T> statement, List<T> parameters) throws SQLException {
        for (var parameter : parameters)
            statement.setValue(parameter);
        return statement;
    }
}
