package pgJDBC.java;

import org.junit.jupiter.api.Test;

import java.sql.*;

public class InitialTest extends TestBase {
    @Test
    void canConnect() throws SQLException {
        var conn = database.createConnection("");
//        var conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
//        var createStatement = conn.createStatement();
//        createStatement.execute("CREATE TABLE IF NOT EXISTS mytable (columnfoo INT)");

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM test_table t WHERE t.required_int = 1");
        while (rs.next()) {
            System.out.print("Column 1 returned ");
            System.out.println(rs.getString(1));
        }
        rs.close();
        st.close();

        var results = Sql
                .from(conn)
                .query("SELECT * FROM test_table t WHERE t.columnfoo = :param")
                .parameters(Sql.parameter("param",Sql.ParameterValue.of( 500)))
                .execute(rowReader -> new Result(rowReader.integer("columnfoo")));
    }

    private record Result(int columnfoo) {
    }

}
