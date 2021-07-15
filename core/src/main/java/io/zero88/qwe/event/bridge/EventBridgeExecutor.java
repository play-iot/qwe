package io.zero88.qwe.event.bridge;

import java.util.function.Consumer;

import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import io.zero88.qwe.HasSharedData;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventBusProxy;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.EventPattern;

public interface EventBridgeExecutor extends EventBusProxy, HasSharedData {

    @Override
    default EventBusClient transporter() {
        return EventBusClient.create(sharedData());
    }

    default Future<EventMessage> execute(EventBridgePlan plan, EventMessage request,
                                         Consumer<EventMessage> resultCallback) {
        if (plan.processPattern() == EventPattern.REQUEST_RESPONSE) {
            return transporter().request(plan.processAddress(), request)
                                .flatMap(r -> callback(r, plan.outboundAddress(), resultCallback));
        }
        return transporter().fire(plan.processAddress(), plan.processPattern(), request)
                            .onSuccess(resultCallback::accept);
    }

    default Future<EventMessage> callback(EventMessage result, String outboundAddr,
                                          Consumer<EventMessage> resultCallback) {
        if (result.isError() || Strings.isBlank(outboundAddr)) {
            resultCallback.accept(result);
            return Future.succeededFuture(result);
        }
        return transporter().fire(outboundAddr, EventPattern.PUBLISH_SUBSCRIBE, result)
                            .onSuccess(resultCallback::accept);
    }

}
