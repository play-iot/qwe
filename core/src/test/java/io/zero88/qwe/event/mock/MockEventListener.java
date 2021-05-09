package io.zero88.qwe.event.mock;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EBContext;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EBParam;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventListener;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class MockEventListener implements EventListener {

    @EBContract(action = "CREATE")
    public Map<String, String> create(RequestData data) {
        return Collections.singletonMap("key", "install");
    }

    @EBContract(action = "UPDATE")
    public Map<String, String> update(RequestData data) {
        return Collections.singletonMap("key", "update");
    }

    public static class MockChildListener extends MockEventListener {

        @EBContract(action = "UPDATE")
        public Map<String, String> update(RequestData data) {
            return Collections.singletonMap("key", "update in child");
        }

    }


    public static class MockWithVariousParams extends MockEventListener {

        @EBContract(action = "NOTIFY")
        public void noReturn(JsonObject data) { }

        @EBContract(action = "GET_LIST")
        public String noParam() { return "hello"; }

        @EBContract(action = "GET_ONE")
        public int javaParam(@EBParam("id") String id) {
            return Integer.parseInt(id);
        }

        @EBContract(action = "CREATE")
        public RequestData refParam(RequestData data) { return data; }

        @EBContract(action = "PATCH")
        public RequestData overrideRefParam(@EBParam("data") RequestData data) { return data; }

        @EBContract(action = "UPDATE")
        public JsonObject twoRefParams(@EBParam("mock") MockParam param, @EBParam("data") RequestData data) {
            return new JsonObject().put("param", JsonObject.mapFrom(param)).put("request", data.toJson());
        }

        @EBContract(action = "REMOVE")
        public JsonObject collectionParam(@EBParam("list") Collection<String> data) {
            JsonObject result = new JsonObject();
            data.forEach(item -> result.put(item, item));
            return result;
        }

    }


    public static class MockDuplicateEvent implements EventListener {

        @EBContract(action = "DUP")
        public Map<String, String> dup1(RequestData data) {
            return Collections.singletonMap("key", "duplicate 1");
        }

        @EBContract(action = "DUP")
        public Map<String, String> dup2(RequestData data) {
            return Collections.singletonMap("key", "duplicate 2");
        }

    }


    public static class MockKeepEventMessageListener implements EventListener {

        @EBContract(action = "MONITOR")
        public int monitor(@EBParam("data") JsonObject json, @EBParam("error") ErrorMessage err) {
            return Objects.isNull(err) ? 1 : 0;
        }

        @EBContract(action = "NOTIFY")
        public int swap(@EBParam("error") ErrorMessage error, @EBParam("data") JsonObject data) {
            return Objects.isNull(error) ? 1 : 0;
        }

    }


    @RequiredArgsConstructor
    public static class MockReceiveSendOrPublish implements EventListener {

        private final String identifier;
        private final Checkpoint cp;

        @EBContract(action = "GET_ONE")
        public JsonObject receive(@EBParam("id") int id) {
            cp.flag();
            System.out.println("[" + identifier + "] receive event");
            return new JsonObject().put("id", id).put("identifier", identifier);
        }

    }


    @RequiredArgsConstructor
    public static class MockFuture implements EventListener {

        private final String identifier;
        private final Checkpoint cp;

        @EBContract(action = "GET_ONE")
        public Future<JsonObject> receive(@EBParam("id") int id) {
            cp.flag();
            System.out.println("[" + identifier + "] receive event");
            return Future.succeededFuture(new JsonObject().put("id", id).put("identifier", identifier));
        }

        @EBContract(action = "CREATE")
        public Future<Void> voidFuture(@EBParam("id") int id) {
            cp.flag();
            System.out.println("[" + identifier + "] receive event");
            return Future.succeededFuture();
        }

    }


    @RequiredArgsConstructor
    public static class MockWithContext implements EventListener {

        private final String identifier;
        private final Checkpoint cp;

        @EBContract(action = "GET_ONE")
        public Future<JsonObject> receive(@EBContext Vertx vertx, @EBParam("id") int id) {
            cp.flag();
            System.out.println("[" + identifier + "] receive event");
            return Future.succeededFuture(new JsonObject().put("id", id).put("identifier", identifier));
        }

        @EBContract(action = "CREATE")
        public Future<Void> voidFuture(@EBContext SharedDataLocalProxy sharedDataProxy, @EBParam("id") int id) {
            cp.flag();
            System.out.println("[" + identifier + "] receive event");
            return Future.succeededFuture();
        }

        @EBContract(action = "UPDATE")
        public Future<Void> voidFuture(@EBContext EventAction action, @EBParam("id") int id) {
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
