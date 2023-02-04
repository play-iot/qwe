package cloud.playio.qwe.sql.spi.checker;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.SQLDialect;

import io.vertx.core.ServiceHelper;

public final class CheckTableExistLoader {

    private final Map<SQLDialect, TableExistChecker> checkers;

    public CheckTableExistLoader() {
        this.checkers = ServiceHelper.loadFactories(TableExistChecker.class, getClass().getClassLoader())
                                     .stream()
                                     .collect(Collectors.toMap(TableExistChecker::dialect, Function.identity(),
                                                               (c1, c2) -> c2.order() - c1.order() >= 0 ? c2 : c1));
    }

    public TableExistChecker lookup(SQLDialect dialect) {
        return checkers.get(dialect);
    }

}
