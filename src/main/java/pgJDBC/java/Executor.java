package pgJDBC.java;

import pgJDBC.java.prepared.SqlPreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

class Executor {

    public static int execute(SqlBuilder builder) {
        try {
            return doExecute(builder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int doExecute(SqlBuilder builder) throws Exception {
        if (builder.parameters() == null) {
            var statement = builder.connection().connection().createStatement();
            return statement.executeUpdate(builder.queryString());
        } else {
            return from(builder)
                    .executeUpdate();
        }
    }

    public static <T> Collection<T> execute(SqlBuilder builder, Function<RowReader, T> map) {
        try {
            var resultSet = doExecuteQuery(builder);
            var results = new ArrayList<T>();
            var rowReader = new RowReader(resultSet);
            while (resultSet.next()) {
                results.add(map.apply(rowReader));
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ResultSet doExecuteQuery(SqlBuilder builder) throws Exception {
        if (builder.parameters() == null) {
            var statement = builder.connection().connection().createStatement();

            return statement.executeQuery(builder.queryString());
        } else {
            return from(builder)
                    .executeQuery();
        }
    }

    static SqlPreparedStatement<? extends Sql.Parameter> from(SqlBuilder builder) throws SQLException {
        return switch (builder.parameters()) {
            case SqlBuilder.Parameters.Named(var named) -> bla(
                    SqlPreparedStatement.named(builder.connection(), builder.queryString()),
                    named
            );
            case SqlBuilder.Parameters.Positional(var positional) -> bla(
                    SqlPreparedStatement.positional(builder.connection(), builder.queryString()),
                    positional)
            ;
        };
    }

    static <T extends Sql.Parameter> SqlPreparedStatement<T> bla(SqlPreparedStatement<T> statement, List<T> parameters) throws SQLException {
        for (var parameter : parameters)
            statement.setValue(parameter);
        return statement;
    }
}
