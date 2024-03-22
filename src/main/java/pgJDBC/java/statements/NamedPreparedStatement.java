package pgJDBC.java.statements;

import pgJDBC.java.ConnectionConfiguration;
import pgJDBC.java.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class NamedPreparedStatement implements SqlPreparedStatement<Sql.Parameter.Named> {
    private static final Pattern findParametersPattern = Pattern.compile("(?<!')(:[\\w]*)(?!')");

    private final PreparedStatementWrapper statement;
    private final List<String> fields = new ArrayList<String>();

    public NamedPreparedStatement(ConnectionConfiguration conn, String statementWithNames) throws SQLException {
        var matcher = findParametersPattern.matcher(statementWithNames);
        while (matcher.find()) {
            fields.add(matcher.group());
        }
        var replacedSql = statementWithNames.replaceAll(findParametersPattern.pattern(), "?");
        statement = new PreparedStatementWrapper(
                conn.connection().prepareStatement(replacedSql),
                conn.typeMap()
        );
    }

    public void setValue(Sql.Parameter.Named parameter) throws SQLException {
        statement.setValue(getIndex(parameter.name()), parameter.value());
    }

    public ResultSet executeQuery() throws SQLException {
        return statement.executeQuery();
    }

    public boolean executeNonQuery() throws SQLException {
        return statement.executeNonQuery();
    }

    public int executeUpdate() throws SQLException {
        return statement.executeUpdate();
    }

    private int getIndex(String name) {
        return fields.indexOf(name) + 1;
    }

    @Override
    public void close() throws Exception {
        statement.close();
    }
}
