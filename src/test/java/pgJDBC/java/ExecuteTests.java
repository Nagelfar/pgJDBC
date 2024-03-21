package pgJDBC.java;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


public class ExecuteTests extends TestBase {
    private static ResultRow extractRow(RowReader rowReader) {
        return new ResultRow(
                rowReader.integer("required_int"),
                rowReader.integerOptional("optional_int"),
                rowReader.string("required_string"),
                rowReader.stringOptional("optional_string"),
                rowReader.uuid("required_uuid"),
                rowReader.uuidOptional("optional_uuid")
        );
    }

    @Test
    void can_execute_on_empty_query_result() throws SQLException {
        withConnection(connection -> {
            var result =
                    Sql.from(connection)
                            .query("SELECT * FROM test_table WHERE 1 = 2")
                            .execute(rowReader -> new Object());

            assertThat(result).isEmpty();
        });
    }

    @Test
    void can_receive_single_row_result_with_all_values_present() throws SQLException {
        withConnection(connection -> {
            var result =
                    Sql.from(connection)
                            .query("SELECT * FROM test_table WHERE required_int = 1")
                            .execute(ExecuteTests::extractRow);

            assertThat(result).containsOnly(ResultRow.FirstResult);
        });
    }

    @Test
    void can_receive_multiple_rows_result_with_some_optional_values_present() throws SQLException {
        withConnection(connection -> {
            var result =
                    Sql.from(connection)
                            .query("SELECT * FROM test_table")
                            .execute(ExecuteTests::extractRow);


            assertThat(result)
                    .hasSize(2)
                    .containsExactly(ResultRow.FirstResult, ResultRow.SecondResult);
        });
    }

    @Test
    void can_use_positional_parameters_to_filter() {
        withConnection(connection -> {
            var result =
                    Sql.from(connection)
                            .query("SELECT * FROM test_table WHERE required_int = ?")
                            .parameters(Sql.ParameterValue.of(2))
                            .execute(ExecuteTests::extractRow);


            assertThat(result)
                    .hasSize(1)
                    .containsOnly(ResultRow.SecondResult);
        });
    }

    @Test
    void can_use_optional_positional_parameters_to_filter() {
        withConnection(connection -> {
            var result =
                    Sql.from(connection)
                            .query("SELECT * FROM test_table WHERE optional_int = ?")
                            .parameters(Sql.ParameterValue.of(null))
                            .execute(ExecuteTests::extractRow);

            assertThat(result).isEmpty();
        });
    }
}
