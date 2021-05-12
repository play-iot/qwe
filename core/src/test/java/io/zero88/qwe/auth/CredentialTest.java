package io.zero88.qwe.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.zero88.qwe.auth.credential.BasicCredential;
import io.zero88.qwe.auth.credential.TokenCredential;
import io.zero88.qwe.dto.JsonData;
import io.vertx.core.json.JsonObject;

class CredentialTest {

    @Test
    void test_basic_serialize() {
        final BasicCredential cred = BasicCredential.builder().user("abc").password("xxy").build();
        Assertions.assertEquals(Credential.CredentialType.BASIC, cred.getType());
        Assertions.assertEquals("abc", cred.getUser());
        Assertions.assertEquals("xxy", cred.getPassword());
        Assertions.assertEquals("Type[BASIC]::User[abc]::Password[******]", cred.toString());
        Assertions.assertEquals(new JsonObject("{\"user\":\"abc\",\"headerAuthType\":\"Basic\",\"type\":\"BASIC\"}"),
                                cred.toJson());
    }

    @Test
    void test_basic_deserialize() {
        final JsonObject object = new JsonObject("{\"user\":\"hello\",\"password\":\"world\",\"type\":\"BASIC\"}");
        final BasicCredential cred = JsonData.from(object, BasicCredential.class);
        Assertions.assertEquals(Credential.CredentialType.BASIC, cred.getType());
        Assertions.assertEquals("Basic", cred.getHeaderAuthType());
        Assertions.assertEquals("hello", cred.getUser());
        Assertions.assertEquals("world", cred.getPassword());
        Assertions.assertEquals("Type[BASIC]::User[hello]::Password[******]", cred.toString());
    }

    @Test
    void test_token_serialize() {
        final TokenCredential cred = TokenCredential.builder().user("abc").token("xxy").build();
        Assertions.assertEquals(Credential.CredentialType.TOKEN, cred.getType());
        Assertions.assertEquals("abc", cred.getUser());
        Assertions.assertEquals("xxy", cred.getToken());
        Assertions.assertEquals("Type[TOKEN]::User[abc]::Token[**********]", cred.toString());
        Assertions.assertEquals(new JsonObject("{\"user\":\"abc\",\"headerAuthType\":\"Bearer\",\"type\":\"TOKEN\"}"),
                                cred.toJson());
    }

    @Test
    void test_token_deserialize() {
        final JsonObject object = new JsonObject(
            "{\"user\":\"hello\",\"token\":\"world\",\"type\":\"TOKEN\",\"headerAuthType\":\"Custom\"}");
        final TokenCredential cred = JsonData.from(object, TokenCredential.class);
        Assertions.assertEquals(Credential.CredentialType.TOKEN, cred.getType());
        Assertions.assertEquals("Custom", cred.getHeaderAuthType());
        Assertions.assertEquals("hello", cred.getUser());
        Assertions.assertEquals("world", cred.getToken());
        Assertions.assertEquals("Type[TOKEN]::User[hello]::Token[**********]", cred.toString());
    }

}
