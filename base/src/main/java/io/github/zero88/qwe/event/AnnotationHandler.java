package io.github.zero88.qwe.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.qwe.dto.ErrorMessage;
import io.github.zero88.qwe.dto.JsonData.SerializerFunction;
import io.github.zero88.qwe.event.EventContractor.Param;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.exceptions.DesiredException;
import io.github.zero88.qwe.exceptions.ErrorCode;
import io.github.zero88.qwe.exceptions.ImplementationError;
import io.github.zero88.qwe.exceptions.UnsupportedException;
import io.github.zero88.qwe.exceptions.converter.CarlExceptionConverter;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Reflections;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Reflections.ReflectionMethod;
import io.github.zero88.utils.Reflections.ReflectionMethod.MethodInfo;
import io.github.zero88.utils.Strings;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

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
               ReflectionClass.belongsTo(clazz.getName(), "io.vertx", "io.reactivex", "io.netty",
                                         "com.fasterxml.jackson");
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
                       Stream.of(contractor.action()).map(EventAction::parse).anyMatch(action::equals);
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
                throw new UnsupportedException("Unsupported event " + action);
            }
            MethodInfo methodInfo = getMethodByAnnotation(eventHandler.getClass(), action);
            Object response = ReflectionMethod.execute(eventHandler, methodInfo.getMethod(), methodInfo.getOutput(),
                                                       methodInfo.getParams().values(),
                                                       parseInputMessage(message, methodInfo.getParams()));
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
     * @throws CarlException if message format is invalid
     */
    private Object[] parseInputMessage(EventMessage message, Map<String, Class<?>> params) {
        if (params.isEmpty()) {
            return new Object[] {};
        }
        JsonObject data = message.isError() ? Optional.ofNullable(message.getError())
                                                      .map(ErrorMessage::toJson)
                                                      .orElse(null) : message.getData();
        if (Objects.isNull(data)) {
            throw new IllegalArgumentException(Strings.format("Event Message Data is null: {0}", message.toJson()));
        }
        if (params.size() == 1) {
            return new Object[] {convertParam(data, params.entrySet().iterator().next(), true)};
        }
        if (params.size() == 2 && JsonObject.class.equals(params.get("data")) &&
            ErrorMessage.class.equals(params.get("error"))) {
            final String first = params.entrySet().iterator().next().getKey();
            return "data".equals(first)
                   ? new Object[] {message.getData(), message.getError()}
                   : new Object[] {message.getError(), message.getData()};
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
            throw new CarlException(ErrorCode.INVALID_ARGUMENT, "Message format is invalid", new HiddenException(e));
        }
    }

    private Object tryParseWithoutParam(JsonObject data, ObjectMapper mapper, Class<?> paramClass) {
        try {
            return mapper.convertValue(data.getMap(), paramClass);
        } catch (IllegalArgumentException e) {
            throw new CarlException(ErrorCode.INVALID_ARGUMENT, "Message format is invalid", new HiddenException(e));
        }
    }

    private EventMessage convertError(Throwable throwable, EventAction action, Logger logger) {
        if (throwable instanceof DesiredException) {
            logger.debug("Failed when handle event {}", action, throwable);
        } else if (throwable instanceof ImplementationError) {
            logger.error("Failed when handle event {}", action, throwable);
        } else {
            logger.warn("Failed when handle event {}", action, throwable);
        }
        final String overrideMsg = throwable instanceof ImplementationError ? "No reply from event " + action : "";
        return EventMessage.error(action, CarlExceptionConverter.friendly(throwable, overrideMsg));
    }

}
