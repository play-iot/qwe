package io.zero88.qwe.auth;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.repl.ReflectionMethod;
import io.github.zero88.utils.Strings;
import io.zero88.qwe.auth.mock.MockAuth.MockAuth1;
import io.zero88.qwe.auth.mock.MockAuth.MockAuth2;
import io.zero88.qwe.auth.mock.MockAuth.MockAuthInClass;
import io.zero88.qwe.auth.mock.another.MockAuthAnother;
import io.zero88.qwe.auth.mock.another.MockNoAuth;

class AuthAnnotationProcessorTest {

    AuthAnnotationProcessor processor = AuthAnnotationProcessor.create();

    @Test
    void test_authz_in_method_with_single_role() {
        final List<AuthReqDefinition> definitions = processor.lookup(findMethod(MockAuth1.class, "m1"));
        Assertions.assertEquals(1, definitions.size());
        doAssert(definitions.get(0), Collections.singletonList("admin"), null, null, null);
    }

    @Test
    void test_authz_in_inherit_method() {
        final List<AuthReqDefinition> definitions = processor.lookup(findMethod(MockAuth2.class, "m1"));
        Assertions.assertEquals(1, definitions.size());
        doAssert(definitions.get(0), Collections.singletonList("super_user"), Collections.singletonList("m1"), null,
                 null);
    }

    @Test
    void test_authz_repeat_in_method() {
        final List<AuthReqDefinition> definitions = processor.lookup(findMethod(MockAuth2.class, "m3"));
        Assertions.assertEquals(2, definitions.size());
        doAssert(definitions.get(0), null, Arrays.asList("m3", "mm"), null, null);
        doAssert(definitions.get(1), null, null, null, "any");
    }

    @Test
    void test_authz_in_package() {
        final List<AuthReqDefinition> definitions = processor.lookup(findMethod(MockAuth1.class, "m2"));
        Assertions.assertEquals(1, definitions.size());
        doAssert(definitions.get(0), null, null, Collections.singletonList("package"), null);
    }

    @Test
    void test_authz_in_class() {
        final List<AuthReqDefinition> definitions = processor.lookup(findMethod(MockAuthInClass.class, "noop"));
        Assertions.assertEquals(1, definitions.size());
        doAssert(definitions.get(0), null, null, Collections.singletonList("happy"), null);
    }

    @Test
    void test_authn_only_in_class() {
        final List<AuthReqDefinition> definitions = processor.lookup(findMethod(MockAuthAnother.class, "access"));
        Assertions.assertEquals(1, definitions.size());
        doAssert(definitions.get(0), null, null, null, null);
    }

    @Test
    void test_authz_in_method_under_authn_in_class() {
        final List<AuthReqDefinition> definitions = processor.lookup(findMethod(MockAuthAnother.class, "create"));
        Assertions.assertEquals(1, definitions.size());
        doAssert(definitions.get(0), null, null, null, "any");
    }

    @Test
    void test_no_auth() {
        final List<AuthReqDefinition> definitions = processor.lookup(findMethod(MockNoAuth.class, "noop"));
        Assertions.assertTrue(definitions.isEmpty());
    }

    private Method findMethod(Class<?> cls, String methodName) {
        Method method = ReflectionMethod.find(m -> m.getName().equals(methodName), cls).findFirst().orElse(null);
        if (method == null) {
            Assertions.fail("Not found method [" + methodName + "] in [" + cls.getName() + "]");
        }
        return method;
    }

    private void doAssert(AuthReqDefinition definition, List<String> roles, List<String> perms, List<String> groups,
                          String custom) {
        Assertions.assertTrue(definition.isLoginRequired());
        if (Objects.nonNull(roles)) {
            Assertions.assertEquals(roles, definition.getAllowRoles());
        } else {
            Assertions.assertTrue(Objects.isNull(definition.getAllowRoles()) || definition.getAllowRoles().isEmpty());
        }
        if (Objects.nonNull(perms)) {
            Assertions.assertEquals(perms, definition.getAllowPerms());
        } else {
            Assertions.assertTrue(Objects.isNull(definition.getAllowPerms()) || definition.getAllowPerms().isEmpty());
        }
        if (Objects.nonNull(groups)) {
            Assertions.assertEquals(groups, definition.getAllowGroups());
        } else {
            Assertions.assertTrue(Objects.isNull(definition.getAllowGroups()) || definition.getAllowGroups().isEmpty());
        }
        if (Objects.nonNull(custom)) {
            Assertions.assertEquals(custom, definition.getCustomAccessRule());
        } else {
            Assertions.assertTrue(Strings.isBlank(definition.getCustomAccessRule()));
        }
    }

}
