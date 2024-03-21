package pgJDBC.java;

import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public record SqlBuilder(
        Connection connection,
        String queryString,
        Sql.Parameter parameter
) implements AutoCloseable {
    // language=SQL

    /**
     * @param sql
     * @return
     */
    public SqlBuilder query(@Language("SQL") String sql) {
        return new SqlBuilder(connection, sql, parameter);
    }

    public SqlBuilder parameters(Sql.Parameter.NamedParameter... parameters) {
        return new SqlBuilder(
                connection,
                queryString,
                new Sql.Parameter.NamedParameters(List.of(parameters))
        );
    }

    public SqlBuilder parameters(Sql.ParameterValue... parameters) {
        return new SqlBuilder(
                connection,
                queryString,
                new Sql.Parameter.PositionalParameters(List.of(parameters))
        );
    }

    public int executeNonQuery() {
        return ExecuteNonQuery.execute(this);
    }

    public <T> Collection<T> execute(Function<RowReader, T> map) {
        return ExecuteQuery.execute(this, map);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
