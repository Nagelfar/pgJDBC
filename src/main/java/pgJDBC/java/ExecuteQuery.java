package pgJDBC.java;

import pgJDBC.java.prepared.SqlPreparedStatement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

class ExecuteQuery {
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
            switch (builder.parameters()) {
                case SqlBuilder.Parameters.Named(var parameters) -> {
                    var statement = SqlPreparedStatement.named(builder.connection(), builder.queryString());

                    for (var parameter : parameters) {
                        statement.setValue(parameter);
                    }

                    return statement.executeQuery();

                }
                case SqlBuilder.Parameters.Positional(var positionalParameters) -> {
                    var statement =
                            SqlPreparedStatement.positional(
                                    builder.connection(),
                                    builder.queryString()
                            );

                    for (var parameter : positionalParameters)
                        statement.setValue(parameter);

                    return statement.executeQuery();
                }

            }
        }
    }

}
