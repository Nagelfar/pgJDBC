package pgJDBC.java;

import org.intellij.lang.annotations.Language;

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
        return Executor.execute(this);
    }

    public <T> Collection<T> execute(Function<RowReader, T> map) {
        return Executor.execute(this, map);
    }

    @Override
    public void close() throws Exception {
        connection.connection().close();
    }

    sealed interface Parameters {

        record Named(List<Sql.Parameter.Named> parameters) implements Parameters {
            public static Named fromArray(Sql.Parameter.Named[] parameters){
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
