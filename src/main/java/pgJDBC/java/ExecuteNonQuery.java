package pgJDBC.java;

import pgJDBC.java.prepared.SqlPreparedStatement;

class ExecuteNonQuery {

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
            switch (builder.parameters()) {
                case SqlBuilder.Parameters.Named(var parameters) -> {
                    var statement = SqlPreparedStatement.named(builder.connection(), builder.queryString());

                    for (var parameter : parameters) {
                        statement.setValue(parameter);
                    }

                    return statement.executeUpdate();

                }
                case SqlBuilder.Parameters.Positional(var positionalParameters) -> {
                    var statement =
                            SqlPreparedStatement.positional(
                                    builder.connection(),
                                    builder.queryString()
                            );

                    for (var parameter : positionalParameters)
                        statement.setValue(parameter);

                    return statement.executeUpdate();
                }

            }
        }
    }
}
