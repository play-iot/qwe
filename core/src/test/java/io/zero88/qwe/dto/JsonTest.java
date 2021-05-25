package io.zero88.qwe.dto;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

public class JsonTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void inject_global_context_object() throws Exception {
        ContextObject ctx = new ContextObject();
        mapper.setInjectableValues(new InjectableValues.Std().addValue(ContextObject.class, ctx));
        DataNeedingContext data = mapper.readValue("{\"prop\":\"foo\"}", DataNeedingContext.class);
        MatcherAssert.assertThat(data.ctx, sameInstance(ctx));
        MatcherAssert.assertThat(data.prop, equalTo("foo"));
    }

    @Test
    public void inject_local_context_object() throws Exception {
        ContextObject ctx = new ContextObject();
        DataNeedingContext data = mapper.readerFor(DataNeedingContext.class)
                                        .with(new InjectableValues.Std().addValue(ContextObject.class, ctx))
                                        .readValue("{\"prop\":\"foo\"}");
        MatcherAssert.assertThat(data.ctx, sameInstance(ctx));
        MatcherAssert.assertThat(data.prop, equalTo("foo"));
    }

    public static class ContextObject {}


    @AllArgsConstructor
    public static class DataNeedingContext {

        private final ContextObject ctx;
        public String prop;

        private DataNeedingContext(ContextObject ctx) {
            this.ctx = ctx;
        }

        @JsonCreator
        public static DataNeedingContext create(@JacksonInject ContextObject ctx, @JsonProperty("prop") String prop) {
            return new DataNeedingContext(ctx, prop);
        }

    }

}
