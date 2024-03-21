package pgJDBC.java;

import java.sql.SQLException;

class ExecuteNonQuery {

    public static int execute(SqlBuilder builder){
        try {
            return doExecute(builder);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static int doExecute(SqlBuilder builder) throws SQLException {
        if (builder.parameter() == null) {
            var statement = builder.connection().createStatement();
            return statement.executeUpdate(builder.queryString());
        } else {
            var statement = builder.connection().prepareStatement(builder.queryString());
            var meta = statement.getParameterMetaData();

            return statement.executeUpdate();
        }
    }
}
