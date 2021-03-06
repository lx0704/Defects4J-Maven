package com.fasterxml.jackson.databind.deser;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * Test to check that customizations work as expected.
 */
@SuppressWarnings("serial")
public class TestCustomDeserializers
    extends BaseMapTest
{
    /*
    /**********************************************************
    /* Helper classes
    /**********************************************************
     */

    static class DummyDeserializer<T>
        extends StdDeserializer<T>
    {
        final T value;

        public DummyDeserializer(T v, Class<T> cls) {
            super(cls);
            value = v;
        }

        @Override
        public T deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
        {
            // need to skip, if structured...
            jp.skipChildren();
            return value;
        }
    }

    static class TestBeans {
        public List<TestBean> beans;
    }
    static class TestBean {
        public CustomBean c;
        public String d;
    }
    @JsonDeserialize(using=CustomBeanDeserializer.class)
    static class CustomBean {
        protected final int a, b;
        public CustomBean(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    static class CustomBeanDeserializer extends JsonDeserializer<CustomBean>
    {
        @Override
        public CustomBean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
        {
            int a = 0, b = 0;
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.START_OBJECT) {
                t = jp.nextToken();
            } else if (t != JsonToken.FIELD_NAME) {
                throw new Error();
            }
            while(t == JsonToken.FIELD_NAME) {
                final String fieldName = jp.getCurrentName();
                t = jp.nextToken();
                if (t != JsonToken.VALUE_NUMBER_INT) {
                    throw new JsonParseException("expecting number got "+ t, jp.getCurrentLocation());
                }
                if (fieldName.equals("a")) {
                    a = jp.getIntValue();
                } else if (fieldName.equals("b")) {
                    b = jp.getIntValue();
                } else {
                    throw new Error();
                }
                t = jp.nextToken();
            }
            return new CustomBean(a, b);
        }
    }

    public static class Immutable {
        protected int x, y;
        
        public Immutable(int x0, int y0) {
            x = x0;
            y = y0;
        }
    }

    // [JACKSON-882]
    public static class CustomKey {
        private final int id;

        public CustomKey(int id) {this.id = id;}

        public int getId() { return id; }
    }
    
    public static class Model
    {
        protected final Map<CustomKey, String> map;

        @JsonCreator
        public Model(@JsonProperty("map") @JsonDeserialize(keyUsing = CustomKeyDeserializer.class) Map<CustomKey, String> map)
        {
            this.map = new HashMap<CustomKey, String>(map);
        }

        @JsonProperty
        @JsonSerialize(keyUsing = CustomKeySerializer.class)
        public Map<CustomKey, String> getMap() {
            return map;
        }
    }
     
    static class CustomKeySerializer extends JsonSerializer<CustomKey> {
        @Override
        public void serialize(CustomKey value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeFieldName(String.valueOf(value.getId()));
        }
    }

    static class CustomKeyDeserializer extends KeyDeserializer {
        @Override
        public CustomKey deserializeKey(String key, DeserializationContext ctxt) throws IOException {
            return new CustomKey(Integer.valueOf(key));
        }
    }

    // [#375]

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Negative { }

    static class Bean375Wrapper {
        @Negative
        public Bean375Outer value;
    }
    
    static class Bean375Outer {
        protected Bean375Inner inner;

        public Bean375Outer(Bean375Inner v) { inner = v; }
    }

    static class Bean375Inner {
        protected int x;

        public Bean375Inner(int x) { this.x = x; }
    }

    static class Bean375OuterDeserializer extends StdDeserializer<Bean375Outer>
        implements ContextualDeserializer
    {
        protected BeanProperty prop;
        
        protected Bean375OuterDeserializer() { this(null); }
        protected Bean375OuterDeserializer(BeanProperty p) {
            super(Bean375Outer.class);
            prop = p;
        }

        @Override
        public Bean375Outer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            Object ob = ctxt.readPropertyValue(p, prop, Bean375Inner.class);
            return new Bean375Outer((Bean375Inner) ob);
        }
        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
                throws JsonMappingException {
            return new Bean375OuterDeserializer(property);
        }
    }

    static class Bean375InnerDeserializer extends StdDeserializer<Bean375Inner>
        implements ContextualDeserializer
    {
        protected boolean negative;
        
        protected Bean375InnerDeserializer() { this(false); }
        protected Bean375InnerDeserializer(boolean n) {
            super(Bean375Inner.class);
            negative = n;
        }

        @Override
        public Bean375Inner deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            int x = jp.getIntValue();
            if (negative) {
                x = -x;
            } else {
                x += x;
            }
            return new Bean375Inner(x);
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
                throws JsonMappingException {
            if (property != null) {
                Negative n = property.getAnnotation(Negative.class);
                if (n != null) {
                    return new Bean375InnerDeserializer(true);
                }
            }
            return this;
        }
    }

    // For [databind#735]
    public static class TestMapBean735 {

        @JsonDeserialize(contentUsing = CustomDeserializer735.class)
        public Map<String, Integer> map1;

        public Map<String, Integer> map2;
    }

    public static class TestListBean735 {

        @JsonDeserialize(contentUsing = CustomDeserializer735.class)
        public List<Integer> list1;

        public List<Integer> list2;
    }
    
    public static class CustomDeserializer735 extends StdDeserializer<Integer> {
        public CustomDeserializer735() {
            super(Integer.class);
        }

        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return 100 * p.getValueAsInt();
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    final ObjectMapper MAPPER = objectMapper();
    
    public void testCustomBeanDeserializer() throws Exception
    {
        String json = "{\"beans\":[{\"c\":{\"a\":10,\"b\":20},\"d\":\"hello, tatu\"}]}";
        TestBeans beans = MAPPER.readValue(json, TestBeans.class);

        assertNotNull(beans);
        List<TestBean> results = beans.beans;
        assertNotNull(results);
        assertEquals(1, results.size());
        TestBean bean = results.get(0);
        assertEquals("hello, tatu", bean.d);
        CustomBean c = bean.c;
        assertNotNull(c);
        assertEquals(10, c.a);
        assertEquals(20, c.b);

        json = "{\"beans\":[{\"c\":{\"b\":3,\"a\":-4},\"d\":\"\"},"
            +"{\"d\":\"abc\", \"c\":{\"b\":15}}]}";
        beans = MAPPER.readValue(json, TestBeans.class);

        assertNotNull(beans);
        results = beans.beans;
        assertNotNull(results);
        assertEquals(2, results.size());

        bean = results.get(0);
        assertEquals("", bean.d);
        c = bean.c;
        assertNotNull(c);
        assertEquals(-4, c.a);
        assertEquals(3, c.b);

        bean = results.get(1);
        assertEquals("abc", bean.d);
        c = bean.c;
        assertNotNull(c);
        assertEquals(0, c.a);
        assertEquals(15, c.b);
    }

    // [Issue#87]: delegating deserializer
    public void testDelegating() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addDeserializer(Immutable.class,
            new StdDelegatingDeserializer<Immutable>(
                new StdConverter<JsonNode, Immutable>() {
                    @Override
                    public Immutable convert(JsonNode value)
                    {
                        int x = value.path("x").asInt();
                        int y = value.path("y").asInt();
                        return new Immutable(x, y);
                    }
                }
                ));

        mapper.registerModule(module);
        Immutable imm = mapper.readValue("{\"x\":3,\"y\":7}", Immutable.class);
        assertEquals(3, imm.x);
        assertEquals(7, imm.y);
    }

    public void testIssue882() throws Exception
    {
        Model original = new Model(Collections.singletonMap(new CustomKey(123), "test"));
        String json = MAPPER.writeValueAsString(original);
        Model deserialized = MAPPER.readValue(json, Model.class);
        assertNotNull(deserialized);
        assertNotNull(deserialized.map);
        assertEquals(1, deserialized.map.size());
    }

    // [#337]: convenience methods for custom deserializers to use
    public void testContextReadValue() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addDeserializer(Bean375Outer.class, new Bean375OuterDeserializer());
        module.addDeserializer(Bean375Inner.class, new Bean375InnerDeserializer());
        mapper.registerModule(module);

        // First, without property; doubles up value:
        Bean375Outer outer = mapper.readValue("13", Bean375Outer.class);
        assertEquals(26, outer.inner.x);

        // then with property; should find annotation, turn negative
        Bean375Wrapper w = mapper.readValue("{\"value\":13}", Bean375Wrapper.class);
        assertNotNull(w.value);
        assertNotNull(w.value.inner);
        assertEquals(-13, w.value.inner.x);
    }

    // [databind#735]: erroneous application of custom deserializer
    public void testCustomMapValueDeser735() throws Exception {
        String json = "{\"map1\":{\"a\":1},\"map2\":{\"a\":1}}";
        TestMapBean735 bean = MAPPER.readValue(json, TestMapBean735.class);

        assertEquals(100, bean.map1.get("a").intValue());
        assertEquals(1, bean.map2.get("a").intValue());
    }

    public void testCustomListValueDeser735() throws Exception {
        String json = "{\"list1\":[1],\"list2\":[1]}";
        TestListBean735 bean = MAPPER.readValue(json, TestListBean735.class);

        assertEquals(100, bean.list1.get(0).intValue());
        assertEquals(1, bean.list2.get(0).intValue());
    }
}
