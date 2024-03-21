package pgJDBC.java;

import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public record SqlBuilder(
        Connection connection,
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

    public SqlBuilder parameters(Sql.NamedParameter... parameters) {
        return new SqlBuilder(
                connection,
                queryString,
                new Parameters.Named(List.of(parameters))
        );
    }

    public SqlBuilder parameters(Sql.Value... parameters) {
        return new SqlBuilder(
                connection,
                queryString,
                new Parameters.Positional(List.of(parameters))
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

    sealed interface Parameters {

        record Named(List<Sql.NamedParameter> parameters) implements Parameters {
        }

        record Positional(List<Sql.Value> parameters) implements Parameters {
        }
    }
}
