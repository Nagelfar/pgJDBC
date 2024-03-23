package pgJDBC.java;

import java.util.List;
import java.util.stream.IntStream;

sealed interface Parameters {

    record NoParameters() implements Parameters {
    }

    record Named(List<Sql.Parameter.Named> parameters) implements Parameters {
        public static Named fromArray(Sql.Parameter.Named[] parameters) {
            return new Named(List.of(parameters));
        }
    }

    record Positional(List<Sql.Parameter.Positional> parameters) implements Parameters {
        public static Positional fromArray(Sql.Value[] parameters) {
            return new Positional(
                    IntStream.range(0, parameters.length)
                            .mapToObj(index -> new Sql.Parameter.Positional(index, parameters[index]))
                            .toList()
            );
        }
    }
}
