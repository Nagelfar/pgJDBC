package pgJDBC.java;

import org.junit.jupiter.api.Test;

import java.util.UUID;

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

    @Test
    void can_execute_an_insert_statement_with_named_parameters() {
        withConnection(connection -> {
            var result =
                    Sql.from(connection)
                            .query("INSERT INTO test_table VALUES (:requiredInt, 3, :requiredString, 'third', :requiredGuid, :optionalGuid)")
                            .parameters(
                                    Sql.parameter(":requiredString", Sql.Value.of("third")),
                                    Sql.parameter(":requiredInt", Sql.Value.of(3)),
                                    Sql.parameter(":optionalGuid", Sql.Value.ofNull(UUID.class)),
                                    Sql.parameter(":requiredGuid", Sql.Value.of(UUID.fromString("00000000-0000-0000-0000-000000000003")))
                            )
                            .executeNonQuery();

            assertThat(result).isEqualTo(1);
        });
    }

    @Test
    void can_execute_an_insert_statement_with_positional_parameters() {
        withConnection(connection -> {
            var result =
                    Sql.from(connection)
                            .query("INSERT INTO test_table VALUES (3, ?, 'third',?, ?, ?)")
                            .parameters(
                                    Sql.Value.ofNull(Integer.class),
                                    Sql.Value.ofNull(String.class),
                                    Sql.Value.of(UUID.fromString("00000000-0000-0000-0000-000000000003")),
                                    Sql.Value.ofNull(UUID.class)
                            )
                            .executeNonQuery();

            assertThat(result).isEqualTo(1);
        });
    }
}
