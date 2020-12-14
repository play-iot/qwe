package io.github.zero88.msa.blueprint.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class EventModelTest {

    @Test(expected = NullPointerException.class)
    public void test_no_address() {
        EventModel.builder().build();
    }

    @Test
    public void test_only_address() {
        EventModel model = EventModel.builder().address("1").build();
        assertEquals("1", model.getAddress());
        assertEquals(EventPattern.REQUEST_RESPONSE, model.getPattern());
        assertFalse(model.isLocal());
        assertEquals(0, model.getEvents().size());
    }

    @Test
    public void test_clone() {
        EventModel model = EventModel.builder().address("1").build();
        EventModel clone1 = EventModel.clone(model, "2");
        EventModel clone2 = EventModel.clone(model, "3", EventPattern.PUBLISH_SUBSCRIBE);
        assertEquals("1", model.getAddress());
        assertEquals("2", clone1.getAddress());
        assertEquals("3", clone2.getAddress());
        assertEquals(EventPattern.REQUEST_RESPONSE, model.getPattern());
        assertEquals(EventPattern.REQUEST_RESPONSE, clone1.getPattern());
        assertEquals(EventPattern.PUBLISH_SUBSCRIBE, clone2.getPattern());
        assertFalse(model.isLocal());
        assertFalse(clone1.isLocal());
        assertFalse(clone2.isLocal());
        assertEquals(0, model.getEvents().size());
        assertEquals(0, clone1.getEvents().size());
        assertEquals(0, clone2.getEvents().size());
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
        assertEquals("1", model.getAddress());
        assertEquals(EventPattern.PUBLISH_SUBSCRIBE, model.getPattern());
        assertTrue(model.isLocal());
        assertEquals(4, model.getEvents().size());
    }

}
