package io.zero88.qwe.event.bridge;

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

    default Future<EventMessage> execute(EventBridgePlan plan, EventMessage request) {
        if (plan.processPattern() == EventPattern.REQUEST_RESPONSE) {
            return transporter().request(plan.processAddress(), request).flatMap(r -> redirectResponse(r, plan));
        }
        return transporter().fire(plan.processAddress(), plan.processPattern(), request);
    }

    default Future<EventMessage> redirectResponse(EventMessage result, EventBridgePlan plan) {
        if (result.isError() || Strings.isBlank(plan.outboundAddress())) {
            return Future.succeededFuture(result);
        }
        return transporter().fire(plan.outboundAddress(), EventPattern.PUBLISH_SUBSCRIBE, result);
    }

}
