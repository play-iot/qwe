package io.zero88.qwe.event.mock;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EventContractor;
import io.zero88.qwe.event.EventContractor.Param;
import io.zero88.qwe.event.EventListener;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class MockEventListener implements EventListener {

    @EventContractor(action = "CREATE")
    public Map<String, String> create(RequestData data) {
        return Collections.singletonMap("key", "install");
    }

    @EventContractor(action = "UPDATE")
    public Map<String, String> update(RequestData data) {
        return Collections.singletonMap("key", "update");
    }

    public static class MockChildListener extends MockEventListener {

        @EventContractor(action = "UPDATE")
        public Map<String, String> update(RequestData data) {
            return Collections.singletonMap("key", "update in child");
        }

    }


    public static class MockWithVariousParams extends MockEventListener {

        @EventContractor(action = "NOTIFY")
        public void noReturn(JsonObject data) { }

        @EventContractor(action = "GET_LIST")
        public String noParam() { return "hello"; }

        @EventContractor(action = "GET_ONE")
        public int javaParam(@Param("id") String id) {
            return Integer.parseInt(id);
        }

        @EventContractor(action = "CREATE")
        public RequestData refParam(RequestData data) { return data; }

        @EventContractor(action = "PATCH")
        public RequestData overrideRefParam(@Param("data") RequestData data) { return data; }

        @EventContractor(action = "UPDATE")
        public JsonObject twoRefParams(@Param("mock") MockParam param, @Param("data") RequestData data) {
            return new JsonObject().put("param", JsonObject.mapFrom(param)).put("request", data.toJson());
        }

        @EventContractor(action = "REMOVE")
        public JsonObject collectionParam(@Param("list") Collection<String> data) {
            JsonObject result = new JsonObject();
            data.forEach(item -> result.put(item, item));
            return result;
        }

    }


    public static class MockDuplicateEvent implements EventListener {

        @EventContractor(action = "DUP")
        public Map<String, String> dup1(RequestData data) {
            return Collections.singletonMap("key", "duplicate 1");
        }

        @EventContractor(action = "DUP")
        public Map<String, String> dup2(RequestData data) {
            return Collections.singletonMap("key", "duplicate 2");
        }

    }


    public static class MockKeepEventMessageListener implements EventListener {

        @EventContractor(action = "MONITOR")
        public int monitor(@Param("data") JsonObject json, @Param("error") ErrorMessage err) {
            return Objects.isNull(err) ? 1 : 0;
        }

        @EventContractor(action = "NOTIFY")
        public int swap(@Param("error") ErrorMessage error, @Param("data") JsonObject data) {
            return Objects.isNull(error) ? 1 : 0;
        }

    }


    @RequiredArgsConstructor
    public static class MockReceiveSendOrPublish implements EventListener {

        private final String identifier;
        private final Checkpoint cp;

        @EventContractor(action = "GET_ONE")
        public JsonObject receive(@Param("id") int id) {
            cp.flag();
            System.out.println("[" + identifier + "] receive event");
            return new JsonObject().put("id", id).put("identifier", identifier);
        }

    }


    @RequiredArgsConstructor
    public static class MockFuture implements EventListener {

        private final String identifier;
        private final Checkpoint cp;

        @EventContractor(action = "GET_ONE")
        public Future<JsonObject> receive(@Param("id") int id) {
            cp.flag();
            System.out.println("[" + identifier + "] receive event");
            return Future.succeededFuture(new JsonObject().put("id", id).put("identifier", identifier));
        }

        @EventContractor(action = "CREATE")
        public Future<Void> voidFuture(@Param("id") int id) {
            cp.flag();
            System.out.println("[" + identifier + "] receive event");
            return Future.succeededFuture();
        }
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class MockParam {

        private int id;
        private String name;

    }

}
