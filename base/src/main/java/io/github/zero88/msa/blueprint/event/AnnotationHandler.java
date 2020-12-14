package io.github.zero88.msa.blueprint.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.msa.blueprint.dto.JsonData.SerializerFunction;
import io.github.zero88.msa.blueprint.event.EventContractor.Param;
import io.github.zero88.msa.blueprint.exceptions.ErrorCode;
import io.github.zero88.msa.blueprint.exceptions.BlueprintException;
import io.github.zero88.msa.blueprint.exceptions.DesiredException;
import io.github.zero88.msa.blueprint.exceptions.ImplementationError;
import io.github.zero88.msa.blueprint.exceptions.converter.BlueprintExceptionConverter;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Reflections;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Reflections.ReflectionMethod;
import io.github.zero88.utils.Reflections.ReflectionMethod.MethodInfo;
import io.github.zero88.utils.Strings;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

final class AnnotationHandler<T extends EventListener> {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationHandler.class);
    private final T eventHandler;
    private final SerializerFunction func;

    AnnotationHandler(T eventHandler) {
        this.eventHandler = eventHandler;
        this.func = SerializerFunction.builder()
                                      .mapper(eventHandler.mapper())
                                      .backupKey(eventHandler.fallback())
                                      .lenient(true)
                                      .build();
    }

    static MethodInfo getMethodByAnnotation(@NonNull Class<?> clazz, @NonNull EventAction action) {
        if (isVertxOrSystemClass(clazz)) {
            throw new ImplementationError(ErrorCode.EVENT_ERROR,
                                          Strings.format("Missing implementation for action {0}", action));
        }
        List<Method> methods = ReflectionMethod.find(clazz, filterMethod(action));
        if (methods.isEmpty()) {
            logger.trace("Try to lookup super class...");
            return getMethodByAnnotation(clazz.getSuperclass(), action);
        }
        if (methods.size() > 1) {
            throw new ImplementationError(ErrorCode.EVENT_ERROR,
                                          Strings.format("More than one methods is marked with action {0} in class {1}",
                                                         action, clazz.getName()));
        }
        return to(methods.get(0));
    }

    private static boolean isVertxOrSystemClass(@NonNull Class<?> clazz) {
        return ReflectionClass.isSystemClass(clazz.getName()) ||
               ReflectionClass.belongsTo("io.vertx.core", "io.netty.", "com.fasterxml.jackson");
    }

    private static MethodInfo to(Method method) {
        EventContractor contractor = method.getAnnotation(EventContractor.class);
        LinkedHashMap<String, Class<?>> inputs = Stream.of(method.getParameters())
                                                       .collect(Collectors.toMap(AnnotationHandler::paramName,
                                                                                 Parameter::getType, throwingMerger(),
                                                                                 LinkedHashMap::new));
        return new MethodInfo(method, contractor.returnType(), inputs);
    }

    private static String paramName(Parameter parameter) {
        Param param = parameter.getAnnotation(Param.class);
        return Objects.nonNull(param) && Strings.isNotBlank(param.value()) ? param.value() : parameter.getName();
    }

    private static Predicate<Method> filterMethod(EventAction action) {
        return Functions.and(Reflections.hasModifiers(Modifier.PUBLIC), Reflections.notModifiers(Modifier.STATIC),
                             Reflections.hasAnnotation(EventContractor.class), method -> {
                EventContractor contractor = method.getAnnotation(EventContractor.class);
                return !ReflectionClass.assertDataType(method.getReturnType(), Void.class) &&
                       ReflectionClass.assertDataType(method.getReturnType(), contractor.returnType()) &&
                       Stream.of(contractor.action()).anyMatch(eventType -> action == EventAction.parse(eventType));
            });
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }

    Single<EventMessage> execute(@NonNull EventMessage message) {
        final EventAction action = message.getAction();
        eventHandler.logger()
                    .debug("Executing action '{}' in listener '{}'", action, eventHandler.getClass().getName());
        try {
            if (!eventHandler.getAvailableEvents().contains(action)) {
                throw new UnsupportedOperationException("Unsupported event " + action);
            }
            MethodInfo methodInfo = getMethodByAnnotation(eventHandler.getClass(), action);
            Object response = ReflectionMethod.execute(eventHandler, methodInfo.getMethod(), methodInfo.getOutput(),
                                                       methodInfo.getParams().values(),
                                                       parseMessage(message, methodInfo.getParams()));
            return convertResult(response).map(data -> EventMessage.success(action, data))
                                          .onErrorReturn(t -> convertError(t, action, eventHandler.logger()));
        } catch (Exception e) {
            return Single.just(convertError(e, action, eventHandler.logger()));
        }
    }

    @SuppressWarnings("unchecked")
    private Single<JsonObject> convertResult(Object response) {
        if (response instanceof Single) {
            return ((Single) response).map(func::apply);
        }
        return Single.just(func.apply(response));
    }

    /**
     * Parse event message to many data inputs
     *
     * @param message Given {@link EventMessage}
     * @param params  Given inputClasses
     * @return data inputs
     * @throws BlueprintException if message format is invalid
     */
    private Object[] parseMessage(EventMessage message, Map<String, Class<?>> params) {
        if (params.isEmpty()) {
            return new Object[] {};
        }
        JsonObject data = message.isError() ? message.getError().toJson() : message.getData();
        if (Objects.isNull(data)) {
            throw new IllegalArgumentException(Strings.format("Event Message Data is null: {0}", message.toJson()));
        }
        if (params.size() == 1) {
            return new Object[] {convertParam(data, params.entrySet().iterator().next(), true)};
        }
        return params.entrySet().stream().map(entry -> convertParam(data, entry, false)).toArray();
    }

    private Object convertParam(JsonObject data, Entry<String, Class<?>> next, boolean oneParam) {
        ObjectMapper mapper = eventHandler.mapper();
        String paramName = next.getKey();
        Class<?> paramClass = next.getValue();
        Object d = data.getValue(paramName);
        if (Objects.isNull(d)) {
            return oneParam ? tryParseWithoutParam(data, mapper, paramClass) : null;
        }
        return tryParseFromParamName(mapper, paramClass, d);
    }

    private Object tryParseFromParamName(ObjectMapper mapper, Class<?> paramClass, Object d) {
        try {
            if (ReflectionClass.isJavaLangObject(paramClass)) {
                if (ReflectionClass.assertDataType(d.getClass(), paramClass)) {
                    return d;
                }
                return paramClass.cast(d);
            }
            return mapper.convertValue(d, paramClass);
        } catch (ClassCastException | IllegalArgumentException e) {
            throw new BlueprintException(ErrorCode.INVALID_ARGUMENT, "Message format is invalid",
                                         new HiddenException(e));
        }
    }

    private Object tryParseWithoutParam(JsonObject data, ObjectMapper mapper, Class<?> paramClass) {
        try {
            return mapper.convertValue(data.getMap(), paramClass);
        } catch (IllegalArgumentException e) {
            throw new BlueprintException(ErrorCode.INVALID_ARGUMENT, "Message format is invalid",
                                         new HiddenException(e));
        }
    }

    private EventMessage convertError(Throwable throwable, EventAction action, Logger logger) {
        if (throwable instanceof DesiredException) {
            logger.debug("Failed when handle event {}", throwable, action);
        } else if (throwable instanceof ImplementationError) {
            logger.error("Failed when handle event {}", throwable, action);
        } else {
            logger.warn("Failed when handle event {}", throwable, action);
        }
        Throwable t = BlueprintExceptionConverter.friendly(throwable, throwable instanceof ImplementationError ?
                                                                      "No reply from event " + action : null);
        return EventMessage.error(action, t);
    }

}
