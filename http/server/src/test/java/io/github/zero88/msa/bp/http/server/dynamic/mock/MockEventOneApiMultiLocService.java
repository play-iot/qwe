package io.github.zero88.msa.bp.http.server.dynamic.mock;

import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.micro.MicroContext;
import io.github.zero88.msa.bp.micro.ServiceDiscoveryController;
import io.github.zero88.msa.bp.micro.metadata.EventMethodDefinition;
import io.reactivex.Single;

public class MockEventOneApiMultiLocService extends MockEventOneApiOneLocService {

    @Override
    public void registerEventbus(EventbusClient controller) {
        controller.register(MockEventServiceListener.TEST_EVENT_4, MockEventServiceListener.TEST_EVENT_LISTENER_4);
    }

    @Override
    protected void publishService(MicroContext microContext) {
        final ServiceDiscoveryController controller = microContext.getLocalController();
        Single.concat(controller.addEventMessageRecord("ems-4", MockEventServiceListener.TEST_EVENT_4.getAddress(),
                                                       EventMethodDefinition.createDefault("/p", "/:pId")),
                      controller.addEventMessageRecord("ems-4", MockEventServiceListener.TEST_EVENT_4.getAddress(),
                                                       EventMethodDefinition.createDefault("/c/:cId/p", "/:pId")))
              .subscribe();
    }

}
