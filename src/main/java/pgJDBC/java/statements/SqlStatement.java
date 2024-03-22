package pgJDBC.java.statements;

import pgJDBC.java.ConnectionConfiguration;
import pgJDBC.java.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SqlStatement extends AutoCloseable {
    ResultSet executeQuery() throws SQLException;

    boolean executeNonQuery() throws SQLException;

    int executeUpdate() throws SQLException;

    static SqlStatement noParameters(ConnectionConfiguration conn, String statementWithNames) throws SQLException {
        return StatementWrapper.build(conn, statementWithNames);
    }
}
