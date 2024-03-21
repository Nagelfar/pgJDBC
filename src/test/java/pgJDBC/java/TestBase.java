package pgJDBC.java;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainerProvider;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

@Testcontainers
public abstract class TestBase {
    @Container
    protected JdbcDatabaseContainer database = new PostgreSQLContainerProvider()
            .newInstance()
            .withInitScript("init.sql");

    protected void withConnection(Consumer<Connection> consumer) {
        try (var connection = database.createConnection("")) {
            consumer.accept(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
