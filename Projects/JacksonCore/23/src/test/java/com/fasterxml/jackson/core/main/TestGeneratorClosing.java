package com.fasterxml.jackson.core.main;


import com.fasterxml.jackson.core.*;

import java.io.*;

/**
 * Set of basic unit tests that verify aspect of closing a
 * {@link JsonGenerator} instance. This includes both closing
 * of physical resources (target), and logical content
 * (json content tree)
 *<p>
 * Specifically, features
 * <code>JsonGenerator.Feature#AUTO_CLOSE_TARGET</code>
 * and
 * <code>JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT</code>
 * are tested.
 */
public class TestGeneratorClosing extends BaseTest
{
    /*
    /**********************************************************
    /* Helper classes
    /**********************************************************
     */

    final static class MyWriter extends StringWriter
    {
        boolean _isClosed = false;

        public MyWriter() { }

        @Override
        public void close() throws IOException {
            _isClosed = true;
            super.close();
        }
        public boolean isClosed() { return _isClosed; }
    }

    final static class MyStream extends ByteArrayOutputStream
    {
        boolean _isClosed = false;

        public MyStream() { }

        @Override
        public void close() throws IOException {
            _isClosed = true;
            super.close();
        }
        public boolean isClosed() { return _isClosed; }
    }

    static class MyBytes extends ByteArrayOutputStream
    {
        public int flushed = 0;

        @Override
        public void flush() throws IOException
        {
            ++flushed;
            super.flush();
        }
    }

    static class MyChars extends StringWriter
    {
        public int flushed = 0;

        @Override
        public void flush()
        {
            ++flushed;
            super.flush();
        }
    }
    
    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */
    
    /**
     * This unit test checks the default behaviour; with no auto-close, no
     * automatic closing should occur, nor explicit one unless specific
     * forcing method is used.
     */
    public void testNoAutoCloseGenerator() throws Exception
    {
        JsonFactory f = new JsonFactory();

        // Check the default settings
        assertTrue(f.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET));
        // then change
        f.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        assertFalse(f.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET));
        @SuppressWarnings("resource")
        MyWriter output = new MyWriter();
        JsonGenerator jg = f.createGenerator(output);

        // shouldn't be closed to begin with...
        assertFalse(output.isClosed());
        jg.writeNumber(39);
        // regular close won't close it either:
        jg.close();
        assertFalse(output.isClosed());
    }

    public void testCloseGenerator() throws Exception
    {
        JsonFactory f = new JsonFactory();
        f.enable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        @SuppressWarnings("resource")
        MyWriter output = new MyWriter();
        JsonGenerator jg = f.createGenerator(output);

        // shouldn't be closed to begin with...
        assertFalse(output.isClosed());
        jg.writeNumber(39);
        // but close() should now close the writer
        jg.close();
        assertTrue(output.isClosed());
    }

    public void testNoAutoCloseOutputStream() throws Exception
    {
        JsonFactory f = new JsonFactory();
        f.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        @SuppressWarnings("resource")
        MyStream output = new MyStream();
        JsonGenerator jg = f.createGenerator(output, JsonEncoding.UTF8);

        assertFalse(output.isClosed());
        jg.writeNumber(39);
        jg.close();
        assertFalse(output.isClosed());
    }

    public void testAutoCloseArraysAndObjects()
        throws Exception
    {
        JsonFactory f = new JsonFactory();
        // let's verify default setting, first:
        assertTrue(f.isEnabled(StreamWriteFeature.AUTO_CLOSE_CONTENT));
        StringWriter sw = new StringWriter();

        // First, test arrays:
        JsonGenerator jg = f.createGenerator(sw);
        jg.writeStartArray();
        jg.close();
        assertEquals("[]", sw.toString());

        // Then objects
        sw = new StringWriter();
        jg = f.createGenerator(sw);
        jg.writeStartObject();
        jg.close();
        assertEquals("{}", sw.toString());
    }

    public void testNoAutoCloseArraysAndObjects()
        throws Exception
    {
        JsonFactory f = JsonFactory.builder()
                .disable(StreamWriteFeature.AUTO_CLOSE_CONTENT)
                .build();
        StringWriter sw = new StringWriter();
        JsonGenerator jg = f.createGenerator(sw);
        jg.writeStartArray();
        jg.close();
        // shouldn't close
        assertEquals("[", sw.toString());

        // Then objects
        sw = new StringWriter();
        jg = f.createGenerator(sw);
        jg.writeStartObject();
        jg.close();
        assertEquals("{", sw.toString());
    }

    // [JACKSON-401]
    @SuppressWarnings("resource")
    public void testAutoFlushOrNot() throws Exception
    {
        JsonFactory f = new JsonFactory();
        assertTrue(f.isEnabled(StreamWriteFeature.FLUSH_PASSED_TO_STREAM));
        MyChars sw = new MyChars();
        JsonGenerator jg = f.createGenerator(sw);
        jg.writeStartArray();
        jg.writeEndArray();
        assertEquals(0, sw.flushed);
        jg.flush();
        assertEquals(1, sw.flushed);
        jg.close();
        
        // ditto with stream
        MyBytes bytes = new MyBytes();
        jg = f.createGenerator(bytes, JsonEncoding.UTF8);
        jg.writeStartArray();
        jg.writeEndArray();
        assertEquals(0, bytes.flushed);
        jg.flush();
        assertEquals(1, bytes.flushed);
        assertEquals(2, bytes.toByteArray().length);
        jg.close();

        // then disable and we should not see flushing again...
        f = JsonFactory.builder()
                .disable(StreamWriteFeature.FLUSH_PASSED_TO_STREAM)
                .build();
        // first with a Writer
        sw = new MyChars();
        jg = f.createGenerator(sw);
        jg.writeStartArray();
        jg.writeEndArray();
        assertEquals(0, sw.flushed);
        jg.flush();
        assertEquals(0, sw.flushed);
        jg.close();
        assertEquals("[]", sw.toString());

        // and then with OutputStream
        bytes = new MyBytes();
        jg = f.createGenerator(bytes, JsonEncoding.UTF8);
        jg.writeStartArray();
        jg.writeEndArray();
        assertEquals(0, bytes.flushed);
        jg.flush();
        assertEquals(0, bytes.flushed);
        jg.close();
        assertEquals(2, bytes.toByteArray().length);
    }
}
