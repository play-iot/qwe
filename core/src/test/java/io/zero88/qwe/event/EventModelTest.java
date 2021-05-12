package io.zero88.qwe.event;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.zero88.qwe.http.event.EventModel;

public class EventModelTest {

    @Test
    public void test_no_address() {
        Assertions.assertThrows(NullPointerException.class, () -> EventModel.builder().build());
    }

    @Test
    public void test_only_address() {
        EventModel model = EventModel.builder().address("1").build();
        Assertions.assertEquals("1", model.getAddress());
        Assertions.assertEquals(EventPattern.REQUEST_RESPONSE, model.getPattern());
        Assertions.assertFalse(model.isLocal());
        Assertions.assertEquals(0, model.getEvents().size());
    }

    @Test
    public void test_clone() {
        EventModel model = EventModel.builder().address("1").build();
        EventModel clone1 = EventModel.clone(model, "2");
        EventModel clone2 = EventModel.clone(model, "3", EventPattern.PUBLISH_SUBSCRIBE);
        Assertions.assertEquals("1", model.getAddress());
        Assertions.assertEquals("2", clone1.getAddress());
        Assertions.assertEquals("3", clone2.getAddress());
        Assertions.assertEquals(EventPattern.REQUEST_RESPONSE, model.getPattern());
        Assertions.assertEquals(EventPattern.REQUEST_RESPONSE, clone1.getPattern());
        Assertions.assertEquals(EventPattern.PUBLISH_SUBSCRIBE, clone2.getPattern());
        Assertions.assertFalse(model.isLocal());
        Assertions.assertFalse(clone1.isLocal());
        Assertions.assertFalse(clone2.isLocal());
        Assertions.assertEquals(0, model.getEvents().size());
        Assertions.assertEquals(0, clone1.getEvents().size());
        Assertions.assertEquals(0, clone2.getEvents().size());
    }

    @Test
    public void test_full() {
        EventModel model = EventModel.builder()
                                     .address("1")
                                     .local(true)
                                     .pattern(EventPattern.PUBLISH_SUBSCRIBE)
                                     .event(EventAction.GET_ONE)
                                     .event(EventAction.GET_ONE)
                                     .event(EventAction.GET_LIST)
                                     .events(Arrays.asList(EventAction.GET_ONE, EventAction.UPDATE, EventAction.CREATE,
                                                           null))
                                     .build();
        Assertions.assertEquals("1", model.getAddress());
        Assertions.assertEquals(EventPattern.PUBLISH_SUBSCRIBE, model.getPattern());
        Assertions.assertTrue(model.isLocal());
        Assertions.assertEquals(4, model.getEvents().size());
    }

}
