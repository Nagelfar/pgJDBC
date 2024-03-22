package pgJDBC.java.statements;

import pgJDBC.java.ConnectionConfiguration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

record StatementWrapper(Statement statement, String query) implements SqlStatement {

    public static StatementWrapper build(ConnectionConfiguration configuration, String query) throws SQLException {
        return new StatementWrapper(
                configuration.connection().createStatement(),
                query
        );
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return statement.executeQuery(query);
    }

    @Override
    public boolean executeNonQuery() throws SQLException {
        return statement.execute(query);
    }

    @Override
    public int executeUpdate() throws SQLException {
        return statement.executeUpdate(query);
    }

    @Override
    public void close() throws Exception {
        statement.close();
    }
}
