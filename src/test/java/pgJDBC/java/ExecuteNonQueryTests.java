package pgJDBC.java;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExecuteNonQueryTests extends TestBase {

    @Test
    void can_execute_an_insert_statement_without_parameters() {
        withConnection(connection -> {
            var result =
                    Sql.from(connection)
                            .query("INSERT INTO test_table VALUES (3, 3, 'third', 'third', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003')")
                            .executeNonQuery();

            assertThat(result).isEqualTo(1);
        });
    }
}
