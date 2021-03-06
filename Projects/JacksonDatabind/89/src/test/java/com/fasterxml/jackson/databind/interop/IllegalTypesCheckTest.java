package com.fasterxml.jackson.databind.interop;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.*;

/**
 * Test case(s) to guard against handling of types that are illegal to handle
 * due to security constraints.
 */
public class IllegalTypesCheckTest extends BaseMapTest
{
    static class Bean1599 {
        public int id;
        public Object obj;
    }

    static class PolyWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
                include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object v;
    }
    
    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = objectMapper();
    
    // // // Tests for [databind#1599]

    public void testXalanTypes1599() throws Exception
    {
        final String clsName = "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl";
        final String JSON = aposToQuotes(
 "{'id': 124,\n"
+" 'obj':[ '"+clsName+"',\n"
+"  {\n"
+"    'transletBytecodes' : [ 'AAIAZQ==' ],\n"
+"    'transletName' : 'a.b',\n"
+"    'outputProperties' : { }\n"
+"  }\n"
+" ]\n"
+"}"
        );
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        try {
            mapper.readValue(JSON, Bean1599.class);
            fail("Should not pass");
        } catch (JsonMappingException e) {
            _verifySecurityException(e, clsName);
        }
    }

    // // // Tests for [databind#1737]

    public void testJDKTypes1737() throws Exception
    {
        _testTypes1737(java.util.logging.FileHandler.class);
        _testTypes1737(java.rmi.server.UnicastRemoteObject.class);
    }

    // 17-Aug-2017, tatu: Ideally would test handling of 3rd party types, too,
    //    but would require adding dependencies. This may be practical when
    //    checking done by module, but for now let's not do that for databind.

    /*
    public void testSpringTypes1737() throws Exception
    {
        _testTypes1737("org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor");
        _testTypes1737("org.springframework.beans.factory.config.PropertyPathFactoryBean");
    }

    public void testC3P0Types1737() throws Exception
    {
        _testTypes1737("com.mchange.v2.c3p0.JndiRefForwardingDataSource");
        _testTypes1737("com.mchange.v2.c3p0.WrapperConnectionPoolDataSource");
    }
    */

    private void _testTypes1737(Class<?> nasty) throws Exception {
        _testTypes1737(nasty.getName());
    }

    private void _testTypes1737(String clsName) throws Exception
    {
        // While usually exploited via default typing let's not require
        // it here; mechanism still the same
        String json = aposToQuotes(
                "{'v':['"+clsName+"','/tmp/foobar.txt']}"
                );
        try {
            MAPPER.readValue(json, PolyWrapper.class);
            fail("Should not pass");
        } catch (JsonMappingException e) {
            _verifySecurityException(e, clsName);
        }
    }

    protected void _verifySecurityException(Throwable t, String clsName) throws Exception
    {
        // 17-Aug-2017, tatu: Expected type more granular in 2.9 (over 2.8)
        _verifyException(t, JsonMappingException.class,
            "Illegal type",
            "to deserialize",
            "prevented for security reasons");
        verifyException(t, clsName);
    }

    protected void _verifyException(Throwable t, Class<?> expExcType,
            String... patterns) throws Exception
    {
        Class<?> actExc = t.getClass();
        if (!expExcType.isAssignableFrom(actExc)) {
            fail("Expected Exception of type '"+expExcType.getName()+"', got '"
                    +actExc.getName()+"', message: "+t.getMessage());
        }
        for (String pattern : patterns) {
            verifyException(t, pattern);
        }
    }
}
