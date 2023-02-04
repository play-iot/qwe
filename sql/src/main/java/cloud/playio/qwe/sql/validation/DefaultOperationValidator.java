package cloud.playio.qwe.sql.validation;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import org.jooq.Record;

import io.vertx.core.Future;
import cloud.playio.qwe.dto.msg.RequestData;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DefaultOperationValidator<R extends Record> implements OperationValidator<R> {

    @NonNull
    private final BiFunction<RequestData, R, Future<R>> validation;
    private OperationValidator<R> andThen;

    @Override
    public @NonNull Future<R> validate(@NonNull RequestData reqData, R dbEntity) {
        return validation.apply(reqData, dbEntity)
                         .flatMap(p -> Optional.ofNullable(andThen)
                                               .map(validator -> validator.validate(reqData, p))
                                               .orElse(Future.succeededFuture(p)));
    }

    @Override
    public @NonNull OperationValidator<R> andThen(OperationValidator<R> andThen) {
        this.andThen = Objects.isNull(this.andThen) ? andThen : this.andThen.andThen(andThen);
        return this;
    }

}
