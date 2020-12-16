package io.github.zero88.msa.bp.http.server.dynamic.mock;

import io.github.zero88.msa.bp.component.ContainerVerticle;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.micro.MicroContext;
import io.github.zero88.msa.bp.micro.MicroserviceProvider;
import io.github.zero88.msa.bp.micro.ServiceDiscoveryController;
import io.github.zero88.msa.bp.micro.metadata.EventMethodDefinition;
import io.reactivex.Single;

public class MockEventOneApiOneLocService extends ContainerVerticle {

    public String configFile() { return "eventService.json"; }

    @Override
    public void start() {
        super.start();
        addProvider(new MicroserviceProvider(), this::publishService);
    }

    @Override
    public void registerEventbus(EventbusClient controller) {
        controller.register(MockEventServiceListener.TEST_EVENT_1, MockEventServiceListener.TEST_EVENT_LISTENER_1);
        controller.register(MockEventServiceListener.TEST_EVENT_2, MockEventServiceListener.TEST_EVENT_LISTENER_2);
        controller.register(MockEventServiceListener.TEST_EVENT_3, MockEventServiceListener.TEST_EVENT_LISTENER_3);
    }

    protected void publishService(MicroContext microContext) {
        final ServiceDiscoveryController controller = microContext.getLocalController();
        Single.concat(controller.addEventMessageRecord("ems-1", MockEventServiceListener.TEST_EVENT_1.getAddress(),
                                                       EventMethodDefinition.createDefault("/hey", "/:id")),
                      controller.addEventMessageRecord("ems-2", MockEventServiceListener.TEST_EVENT_2.getAddress(),
                                                       EventMethodDefinition.createDefault("/c/:cId/p", "/:pId")),
                      controller.addEventMessageRecord("ems-3", MockEventServiceListener.TEST_EVENT_3.getAddress(),
                                                       EventMethodDefinition.createDefault("/x/:xId/y", "/:yId",
                                                                                           false))).subscribe();
    }

}
