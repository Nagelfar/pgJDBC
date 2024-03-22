package pgJDBC.java;

import java.sql.Types;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

public record TypeMap(Map<Class<?>, Integer> map) {
    public static TypeMap defaultTypeMap = new TypeMap(
            Map.of(
                    Integer.class, Types.INTEGER,
                    UUID.class, Types.OTHER
                )
    );

    public OptionalInt findSqlTypeOf(Class<?> type) {
        return Optional.ofNullable(map.get(type)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
    }

    public int getSqlTypeOf(Class<?> type) {
        return findSqlTypeOf(type)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find a sql type for " + type));
    }

}
