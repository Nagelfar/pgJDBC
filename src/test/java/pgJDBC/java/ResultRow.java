package pgJDBC.java;

import java.util.Optional;
import java.util.UUID;

public record ResultRow(
        Integer required_int,
        Optional<Integer> optional_int,
        String required_string,
        Optional<String> optional_string,
        UUID required_uuid,
        Optional<UUID> optional_uuid
) {

    public static final ResultRow FirstResult = new ResultRow(
            1,
            Optional.of(1),
            "first",
            Optional.of("first"),
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000001"))
    );

    public static final ResultRow SecondResult = new ResultRow(
            2,
            Optional.empty(),
            "second",
            Optional.empty(),
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            Optional.empty()
    );
}
