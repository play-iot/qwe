package io.zero88.qwe.storage.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.zero88.qwe.utils.Configs;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;

class JsonPointerTest {

    @Test
    void test_read_all_json() {
        final JsonPointer pointer = JsonPointer.create();
        Assertions.assertTrue(pointer.isRootPointer());
        Assertions.assertTrue(pointer.isLocalPointer());
        final Object obj = pointer.queryJson(Configs.loadJsonConfig("jp.json"));
        Assertions.assertEquals("{\"abc\":{\"1\":\"a\",\"2\":\"b\"},\"xyz\":{},\"array\":[1],\"array2\":[1,2]}",
                                obj.toString());
    }

    @Test
    void test_read_json_pointer() {
        final JsonPointer pointer = JsonPointer.from("/abc/1");
        Assertions.assertFalse(pointer.isRootPointer());
        Assertions.assertTrue(pointer.isParent(JsonPointer.from("/abc/1/2")));
        final Object obj = pointer.queryJson(Configs.loadJsonConfig("jp.json"));
        Assertions.assertEquals("a", obj);
    }

    @Test
    void test_write_into_existing_json_element() {
        final JsonPointer pointer = JsonPointer.from("/xyz");
        final Object obj = pointer.queryJson(Configs.loadJsonConfig("jp.json"));
        Assertions.assertTrue(obj instanceof JsonObject);
        Assertions.assertTrue(((JsonObject) obj).isEmpty());
        final Object alo = pointer.writeJson(obj, new JsonObject().put("alo", 222));
        System.out.println(alo);
        Assertions.assertTrue(alo instanceof JsonObject);
        Assertions.assertEquals(new JsonObject().put("xyz", new JsonObject().put("alo", 222)), alo);
    }

    @Test
    public void testRootPointerWrite() {
        JsonPointer pointer = JsonPointer.create();
        JsonObject obj = new JsonObject();
        JsonObject toInsert = new JsonObject().put("n", 1);
        final Object actual = pointer.writeJson(obj, toInsert, true);
        System.out.println(actual);
        System.out.println(obj);
        Assertions.assertSame(toInsert, actual);
    }

    @Test
    void test_write_new_json_element() {
        final JsonPointer pointer = JsonPointer.from("/hey");
        final JsonObject json = Configs.loadJsonConfig("jp.json");
        final JsonObject toInsert = new JsonObject().put("alo", 222);
        final Object obj = pointer.queryJson(json);
        Assertions.assertNull(obj);

        final Object parent = pointer.copy().parent().queryJson(json);
        System.out.println(parent);
        Assertions.assertEquals(json, parent);
        final Object alo = pointer.writeJson(json, toInsert, true);
        System.out.println(json);
        Assertions.assertEquals(json, alo);
        Assertions.assertEquals(toInsert, pointer.queryJson(json));
    }

    @Test
    public void testWriteWithCreateOnMissingJsonObject() {
        JsonObject obj = new JsonObject().put("hello", new JsonObject().put("world", 1).put("worl", "wrong"))
                                         .put("helo", new JsonObject().put("world", "wrong").put("worl", "wrong"));
        Object toInsert = new JsonObject().put("github", "slinkydeveloper");
        final JsonPointer pointer = JsonPointer.from("/hello/users/francesco");
        Assertions.assertNull(pointer.queryJson(obj));
        Assertions.assertNull(pointer.copy().parent().queryJson(obj));
        Assertions.assertNotNull(pointer.copy().parent().parent().queryJson(obj));
        final Object write = pointer.writeJson(obj, toInsert, true);
        System.out.println(write);
        Assertions.assertEquals(obj, write);
        Assertions.assertEquals(toInsert, pointer.queryJson(obj));
    }

    @Test
    public void testWriteJsonArrayAppend() {
        JsonObject obj = new JsonObject().put("hello", new JsonObject().put("world", 1).put("worl", "wrong"))
                                         .put("helo", new JsonObject().put("world", "wrong").put("worl", "wrong"));
        JsonArray array = new JsonArray();
        array.add(obj);
        array.add(obj);
        Object toInsert = new JsonObject().put("github", "slinkydeveloper");
        Assertions.assertEquals(array, JsonPointer.from("/-").writeJson(array, toInsert));
        Assertions.assertEquals(toInsert, JsonPointer.from("/2").queryJson(array));
        Assertions.assertEquals(array.getValue(0), array.getValue(1));
    }

    @Test
    public void testNestedWriteJsonArraySubstitute() {
        JsonObject obj = new JsonObject().put("hello", new JsonObject().put("world", 1).put("worl", "wrong"))
                                         .put("helo", new JsonObject().put("world", "wrong").put("worl", "wrong"));
        JsonArray array = new JsonArray();
        array.add(obj);
        array.add(obj);
        JsonObject root = new JsonObject().put("array", array);

        Object toInsert = new JsonObject().put("github", "slinkydeveloper");
        Assertions.assertEquals(root, JsonPointer.from("/array/0").writeJson(root, toInsert));
        System.out.println(root);
        Assertions.assertEquals(toInsert, JsonPointer.from("/array/0").queryJson(root));
    }

    @Test
    void test_append_json_array() {
        JsonPointer pointer = JsonPointer.create();
        final JsonPointer arrayPointer = JsonPointer.from("/array");
        final Object root = pointer.queryJson(Configs.loadJsonConfig("jp.json"));
        System.out.println(root);
        final Object newRoot = arrayPointer.copy().append(0).writeJson(root, 2);
        System.out.println(newRoot);
        Assertions.assertTrue(newRoot instanceof JsonObject);
        final Object updated = arrayPointer.queryJson(newRoot);
        Assertions.assertEquals(new JsonArray().add(2).add(1), updated);
    }

    @Test
    void test_remove_item_in_json_array() {
        JsonPointer rootPointer = JsonPointer.create();
        JsonPointer array2Pointer = JsonPointer.from("/array2");
        final Object root = rootPointer.queryJson(Configs.loadJsonConfig("jp.json"));
        System.out.println(root);
        final Object array2 = array2Pointer.queryJson(root);
        System.out.println(array2);
        Assertions.assertTrue(array2 instanceof JsonArray);
        Assertions.assertEquals(2, ((JsonArray) array2).remove(1));
        final JsonArray updated = ((JsonArray) array2).add(3);
        final Object newRoot = array2Pointer.writeJson(root, updated);
        System.out.println(newRoot);
        Assertions.assertTrue(newRoot instanceof JsonObject);
        Assertions.assertEquals(root, newRoot);
        Assertions.assertEquals(root, newRoot);
        final Object newOne = array2Pointer.queryJson(root);
        Assertions.assertEquals(new JsonArray().add(1).add(3), newOne);
    }

}
