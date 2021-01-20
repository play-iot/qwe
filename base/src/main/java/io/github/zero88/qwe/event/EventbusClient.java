package io.github.zero88.qwe.event;

import io.github.zero88.qwe.transport.Transporter;
import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;

import lombok.NonNull;

/**
 * Represents client for event bus.
 * <p>
 * It helps registering {@code listener} for specified address or send message to specified address
 */
public interface EventbusClient extends Shareable, Transporter {

    /**
     * Send message then wait and handle response to specific address
     *
     * @param address      Address
     * @param message      Event message
     * @param replyHandler Reply message handler
     * @see EventPattern#REQUEST_RESPONSE
     */
    default void request(@NonNull String address, @NonNull EventMessage message,
                         @NonNull Handler<AsyncResult<Message<Object>>> replyHandler) {
        request(address, message, replyHandler, null);
    }

    /**
     * Send message then wait and handle response to specific address
     *
     * @param address         Address
     * @param message         Event message
     * @param replyHandler    Reply message handler
     * @param deliveryOptions Delivery options
     * @see EventPattern#REQUEST_RESPONSE
     */
    default void request(@NonNull String address, @NonNull EventMessage message,
                         @NonNull Handler<AsyncResult<Message<Object>>> replyHandler, DeliveryOptions deliveryOptions) {
        fire(address, EventPattern.REQUEST_RESPONSE, message, replyHandler, deliveryOptions);
    }

    /**
     * Send message then return single response message
     *
     * @param address Address
     * @param message Event message
     * @return response message
     * @see EventMessage
     */
    default Single<EventMessage> request(@NonNull String address, @NonNull EventMessage message) {
        return request(address, message, (DeliveryOptions) null);
    }

    /**
     * Send message then return single response message
     *
     * @param event Delivery event
     * @return response message
     * @see EventMessage
     */
    default Single<EventMessage> request(@NonNull DeliveryEvent event) {
        return request(event.getAddress(), event.payload(), (DeliveryOptions) null);
    }

    /**
     * Send message with delivery options then return single response message
     *
     * @param address         Address
     * @param message         Event message
     * @param deliveryOptions delivery options
     * @return response message
     * @see EventMessage
     * @see DeliveryOptions
     */
    Single<EventMessage> request(@NonNull String address, @NonNull EventMessage message,
                                 DeliveryOptions deliveryOptions);

    /**
     * Send message to specific address
     *
     * @param address Address
     * @param message Event message
     * @see EventPattern#POINT_2_POINT
     */
    default void send(@NonNull String address, @NonNull EventMessage message) {
        fire(address, EventPattern.POINT_2_POINT, message);
    }

    /**
     * Publish message to specific address
     *
     * @param address Eventbus address
     * @param message Request data message
     * @see EventPattern#PUBLISH_SUBSCRIBE
     */
    default void publish(@NonNull String address, @NonNull EventMessage message) {
        fire(address, EventPattern.PUBLISH_SUBSCRIBE, message);
    }

    /**
     * Fire the request to specific address
     * <p>
     * It is equivalent to call {@link #fire(DeliveryEvent, Handler)} with no {@code reply handler}
     *
     * @param deliveryEvent Delivery Event
     */
    default void fire(@NonNull DeliveryEvent deliveryEvent) {
        fire(deliveryEvent, null);
    }

    /**
     * Fire the request to specific address
     * <p>
     * It is equivalent to call {@link #fire(DeliveryEvent, DeliveryOptions, Handler)} with {@code deliveryOptions} is
     * {@code null}
     *
     * @param deliveryEvent Delivery Event
     * @param replyHandler  Reply message handler
     */
    default void fire(@NonNull DeliveryEvent deliveryEvent, Handler<AsyncResult<Message<Object>>> replyHandler) {
        fire(deliveryEvent, null, replyHandler);
    }

    /**
     * Fire the request to specific address
     * <p>
     *
     * @param deliveryEvent   Delivery Event
     * @param deliveryOptions Delivery Options
     * @param replyHandler    Reply message handler
     */
    default void fire(@NonNull DeliveryEvent deliveryEvent, DeliveryOptions deliveryOptions,
                      Handler<AsyncResult<Message<Object>>> replyHandler) {
        fire(deliveryEvent.getAddress(), deliveryEvent.getPattern(), deliveryEvent.payload(), replyHandler,
             deliveryOptions);
    }

    /**
     * Fire the request to specific address
     * <p>
     * It is equivalent to call {@link #fire(String, EventPattern, EventMessage, DeliveryOptions)} with {@code
     * deliveryOptions} is {@code null}
     *
     * @param address Eventbus address
     * @param pattern Event pattern
     * @param message Request data message
     */
    default void fire(@NonNull String address, @NonNull EventPattern pattern, @NonNull EventMessage message) {
        fire(address, pattern, message, null, null);
    }

    /**
     * Fire the request to specific address
     * <p>
     * It is equivalent to call {@link #fire(String, EventPattern, EventMessage, Handler, DeliveryOptions)} with {@code
     * deliveryOptions} is {@code null}
     *
     * @param address      Eventbus address
     * @param pattern      Event pattern
     * @param message      Request data message
     * @param replyHandler Reply message handler
     * @see EventPattern
     * @see EventMessage
     * @see #fire(String, EventPattern, EventMessage, Handler, DeliveryOptions)
     */
    default void fire(@NonNull String address, @NonNull EventPattern pattern, @NonNull EventMessage message,
                      Handler<AsyncResult<Message<Object>>> replyHandler) {
        fire(address, pattern, message, replyHandler, null);
    }

    /**
     * Fire event data to specific address
     *
     * @param address         Eventbus address
     * @param pattern         Event pattern
     * @param message         Event message
     * @param deliveryOptions Delivery options
     * @see #fire(String, EventPattern, EventMessage, Handler, DeliveryOptions)
     */
    default void fire(@NonNull String address, @NonNull EventPattern pattern, @NonNull EventMessage message,
                      DeliveryOptions deliveryOptions) {
        fire(address, pattern, message, null, deliveryOptions);
    }

    /**
     * Fire event data to specific address
     * <p>
     *
     * @param address         Eventbus address
     * @param pattern         Event pattern
     * @param message         Event message
     * @param replyHandler    Reply message handler
     * @param deliveryOptions Delivery options
     * @see #fire(String, EventPattern, JsonObject, Handler, DeliveryOptions)
     */
    default void fire(@NonNull String address, @NonNull EventPattern pattern, @NonNull EventMessage message,
                      Handler<AsyncResult<Message<Object>>> replyHandler, DeliveryOptions deliveryOptions) {
        fire(address, pattern, message.toJson(), replyHandler, deliveryOptions);
    }

    /**
     * Fire event data to specific address
     *
     * @param address         Eventbus address
     * @param pattern         Event pattern
     * @param data            Data message
     * @param replyHandler    Reply message handler
     * @param deliveryOptions Delivery options
     */
    void fire(@NonNull String address, @NonNull EventPattern pattern, @NonNull JsonObject data,
              Handler<AsyncResult<Message<Object>>> replyHandler, DeliveryOptions deliveryOptions);

    /**
     * Register event listener with event model.
     *
     * @param eventModel Event model
     * @param handler    Handler when receiving message
     * @return a reference to this, so the API can be used fluently
     * @see EventModel
     */
    default EventbusClient register(@NonNull EventModel eventModel, @NonNull EventListener handler) {
        return this.register(eventModel.getAddress(), eventModel.isLocal(), handler);
    }

    /**
     * Register event listener
     * <p>
     * It is equivalent to call {@link #register(String, boolean, EventListener)} with {@code local} is {@code true}
     *
     * @param address  Event bus address
     * @param listener listener to handle the received message
     * @return a reference to this, so the API can be used fluently
     * @see EventListener
     */
    default EventbusClient register(String address, @NonNull EventListener listener) {
        return this.register(address, true, listener);
    }

    /**
     * Register event listener
     *
     * @param address  Event bus address
     * @param local    If {@code true}, only register for local event address
     * @param listener listener to handle the received message
     * @return a reference to this, so the API can be used fluently
     * @see EventListener
     * @see #register(String, EventListener)
     */
    EventbusClient register(String address, boolean local, @NonNull EventListener listener);

}
