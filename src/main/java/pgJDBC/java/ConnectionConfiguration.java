package pgJDBC.java;

import java.sql.Connection;
import java.sql.Types;
import java.util.Map;

public record ConnectionConfiguration(
        Connection connection,
        TypeMap typeMap
) {

}
