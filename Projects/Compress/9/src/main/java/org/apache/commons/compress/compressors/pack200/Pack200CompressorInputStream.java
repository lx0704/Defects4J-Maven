/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

import org.apache.commons.compress.compressors.CompressorInputStream;

/**
 * An input stream that decompresses from the Pack200 format to be read
 * as any other stream.
 * 
 * @NotThreadSafe
 * @since Apache Commons Compress 1.3
 */
public class Pack200CompressorInputStream extends CompressorInputStream {
    private final InputStream originalInput;
    private final StreamBridge streamBridge;

    /**
     * Decompresses the given stream, caching the decompressed data in
     * memory.
     *
     * <p>When reading from a file the File-arg constructor may
     * provide better performance.</p>
     */
    public Pack200CompressorInputStream(final InputStream in)
        throws IOException {
        this(in, Pack200Strategy.IN_MEMORY);
    }

    /**
     * Decompresses the given stream using the given strategy to cache
     * the results.
     *
     * <p>When reading from a file the File-arg constructor may
     * provide better performance.</p>
     */
    public Pack200CompressorInputStream(final InputStream in,
                                        final Pack200Strategy mode)
        throws IOException {
        this(in, null, mode, null);
    }

    /**
     * Decompresses the given stream, caching the decompressed data in
     * memory and using the given properties.
     *
     * <p>When reading from a file the File-arg constructor may
     * provide better performance.</p>
     */
    public Pack200CompressorInputStream(final InputStream in,
                                        final Map<String, String> props)
        throws IOException {
        this(in, Pack200Strategy.IN_MEMORY, props);
    }

    /**
     * Decompresses the given stream using the given strategy to cache
     * the results and the given properties.
     *
     * <p>When reading from a file the File-arg constructor may
     * provide better performance.</p>
     */
    public Pack200CompressorInputStream(final InputStream in,
                                        final Pack200Strategy mode,
                                        final Map<String, String> props)
        throws IOException {
        this(in, null, mode, props);
    }

    /**
     * Decompresses the given file, caching the decompressed data in
     * memory.
     */
    public Pack200CompressorInputStream(final File f) throws IOException {
        this(f, Pack200Strategy.IN_MEMORY);
    }

    /**
     * Decompresses the given file using the given strategy to cache
     * the results.
     */
    public Pack200CompressorInputStream(final File f, final Pack200Strategy mode)
        throws IOException {
        this(null, f, mode, null);
    }

    /**
     * Decompresses the given file, caching the decompressed data in
     * memory and using the given properties.
     */
    public Pack200CompressorInputStream(final File f,
                                        final Map<String, String> props)
        throws IOException {
        this(f, Pack200Strategy.IN_MEMORY, props);
    }

    /**
     * Decompresses the given file using the given strategy to cache
     * the results and the given properties.
     */
    public Pack200CompressorInputStream(final File f, final Pack200Strategy mode,
                                        final Map<String, String> props)
        throws IOException {
        this(null, f, mode, props);
    }

    private Pack200CompressorInputStream(final InputStream in, final File f,
                                         final Pack200Strategy mode,
                                         final Map<String, String> props)
        throws IOException {
        originalInput = in;
        streamBridge = mode.newStreamBridge();
        JarOutputStream jarOut = new JarOutputStream(streamBridge);
        Pack200.Unpacker u = Pack200.newUnpacker();
        if (props != null) {
            u.properties().putAll(props);
        }
        if (f == null) {
            u.unpack(new FilterInputStream(in) {
                    @Override
                        public void close() {
                        // unpack would close this stream but we
                        // want to give the user code more control
                    }
                },
                jarOut);
        } else {
            u.unpack(f, jarOut);
        }
        jarOut.close();
    }

    /** {@inheritDoc} */
    @Override
    public int read() throws IOException {
        return streamBridge.getInput().read();
    }

    /** {@inheritDoc} */
    @Override
    public int read(byte[] b) throws IOException {
        return streamBridge.getInput().read(b);
    }

    /** {@inheritDoc} */
    @Override
    public int read(byte[] b, int off, int count) throws IOException {
        return streamBridge.getInput().read(b, off, count);
    }

    /** {@inheritDoc} */
    @Override
    public int available() throws IOException {
        return streamBridge.getInput().available();
    }

    /** {@inheritDoc} */
    @Override
    public boolean markSupported() {
        try {
            return streamBridge.getInput().markSupported();
        } catch (IOException ex) {
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mark(int limit) {
        try {
            streamBridge.getInput().mark(limit);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void reset() throws IOException {
        streamBridge.getInput().reset();
    }

    /** {@inheritDoc} */
    @Override
    public long skip(long count) throws IOException {
        return streamBridge.getInput().skip(count);
    }

    @Override
    public void close() throws IOException {
        try {
            streamBridge.stop();
        } finally {
            if (originalInput != null) {
                originalInput.close();
            }
        }
    }

    private static final byte[] CAFE_DOOD = new byte[] {
        (byte) 0xCA, (byte) 0xFE, (byte) 0xD0, (byte) 0x0D
    };

    /**
     * Checks if the signature matches what is expected for a pack200
     * file (0xCAFED00D).
     * 
     * @param signature
     *            the bytes to check
     * @param length
     *            the number of bytes to check
     * @return true, if this stream is a pack200 compressed stream,
     * false otherwise
     */
    public static boolean matches(byte[] signature, int length) {
        if (length < 4) {
            return false;
        }

        for (int i = 0; i < 4; i++) {
            if (signature[i] != CAFE_DOOD[i]) {
                return false;
            }
        }

        return true;
    }
}
