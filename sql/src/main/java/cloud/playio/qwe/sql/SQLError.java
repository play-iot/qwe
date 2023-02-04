package cloud.playio.qwe.sql;

import io.github.zero88.utils.Strings;
import cloud.playio.qwe.exceptions.InitializerError;
import cloud.playio.qwe.exceptions.InitializerError.MigrationError;

public interface SQLError {

    class InitSchemaError extends InitializerError {

        public InitSchemaError(String message, Throwable e) {
            super(Strings.fallback(message, "Error when setting up database schema"), e);
        }

        public InitSchemaError(Throwable e) {
            this(null, e);
        }

    }


    class InitDataError extends InitializerError {

        public InitDataError(String message, Throwable e) {
            super(Strings.fallback(message, "Error when initializing data"), e);
        }

        public InitDataError(Throwable e) {
            this(null, e);
        }

    }


    class SQLMigrationError extends MigrationError {

        public SQLMigrationError(String message, Throwable e) {
            super(Strings.fallback(message, "Error when migrating database"), e);
        }

        public SQLMigrationError(Throwable e) {
            this(null, e);
        }

    }

}
