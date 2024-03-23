package pgJDBC.java;

import org.intellij.lang.annotations.Language;

import java.util.Collection;
import java.util.function.Function;

public interface SqlBuilder {
    SqlBuilder query(@Language("SQL") String sql);

    SqlBuilder parameters(Sql.Parameter.Named... parameters);

    SqlBuilder parameters(Sql.Value... parameters);

    int executeNonQuery();

    <T> Collection<T> execute(Function<RowReader, T> map);
}
