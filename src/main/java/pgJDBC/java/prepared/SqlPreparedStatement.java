package pgJDBC.java.prepared;

import pgJDBC.java.ConnectionConfiguration;
import pgJDBC.java.Sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface SqlPreparedStatement<T extends Sql.Parameter> extends AutoCloseable {
    ResultSet executeQuery() throws SQLException;

    boolean executeNonQuery() throws SQLException;

    int executeUpdate() throws SQLException;

    void setValue(T parameterType) throws SQLException;


    static SqlPreparedStatement<Sql.Parameter.Named> named(ConnectionConfiguration conn, String statementWithNames) throws SQLException {
        return new NamedPreparedStatement(conn, statementWithNames);
    }

    static SqlPreparedStatement<Sql.Parameter.Positional> positional(ConnectionConfiguration conn, String statement) throws SQLException {
        return PreparedStatementWrapper.build(conn, statement);
    }
}
