package pgJDBC.java.prepared;

import pgJDBC.java.ConnectionConfiguration;
import pgJDBC.java.Sql;
import pgJDBC.java.TypeMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

record PreparedStatementWrapper(
        PreparedStatement statement,
        TypeMap typeMap)
        implements SqlPreparedStatement<Sql.Parameter.Positional> {

    public static PreparedStatementWrapper build(ConnectionConfiguration configuration, String query) throws SQLException {
        return new PreparedStatementWrapper(
                configuration.connection().prepareStatement(query),
                configuration.typeMap()
        );
    }

    public void setValue(int index, Sql.Value value) throws SQLException {
        switch (value) {
            case Sql.Value.Null(var type) -> statement.setNull(index, typeMap.getSqlTypeOf(type));
            case Sql.Value.SqlInt(var anInt) -> statement.setInt(index, anInt);
            case Sql.Value.SqlString(var string) -> statement.setString(index, string);
            case Sql.Value.SqlUUID(var uuid) -> statement.setObject(index, uuid);
        }
    }

    public void setValue(Sql.Parameter.Positional parameter) throws SQLException {
        setValue(parameter.position() + 1, parameter.value());
    }

    public ResultSet executeQuery() throws SQLException {
        return statement.executeQuery();
    }

    public boolean executeNonQuery() throws SQLException {
        return statement.execute();
    }

    public int executeUpdate() throws SQLException {
        return statement.executeUpdate();
    }

    public void close() throws SQLException {
        statement.close();
    }
}
