package pgJDBC.java;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class ExecuteQuery {
    public static <T> Collection<T> execute(SqlBuilder builder, Function<RowReader, T> map) {
        try {
            var resultSet = doExecuteQuery(builder);
            var results = new ArrayList<T>();
            while (resultSet.next()) {
                var row = new RowReader(resultSet);
                results.add(map.apply(row));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static ResultSet doExecuteQuery(SqlBuilder builder) throws SQLException {
        if (builder.parameter() == null) {
            var statement = builder.connection().createStatement();

            return statement.executeQuery(builder.queryString());
        } else {
            var statement = builder.connection().prepareStatement(builder.queryString());

            var meta = statement.getParameterMetaData();

            switch (builder.parameter()) {
                case Sql.Parameter.NamedParameters namedParameters -> {

                }
                case Sql.Parameter.PositionalParameters(var positionalParameters) -> {
                    for (int i = 0; i < positionalParameters.size(); i++) {
                        applyParameter(statement, i + 1, positionalParameters.get(i));
                    }
                }
            }

            return statement.executeQuery();

        }


    }

    private static void applyParameter(PreparedStatement statement, int index, Sql.ParameterValue parameter) throws SQLException {
        switch (parameter) {
            case Sql.ParameterValue.Int(var anInt) -> statement.setInt(index, anInt);
            case Sql.ParameterValue.Null(var type) -> statement.setNull(index, type);
        }
    }

}
