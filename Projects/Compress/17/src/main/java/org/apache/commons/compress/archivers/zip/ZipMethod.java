/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.commons.compress.archivers.zip;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;

/**
 * List of known compression methods 
 * 
 * Many of these methods are currently not supported by commons compress
 * 
 * @since 1.5
 */
public enum ZipMethod {

    /**
     * Compression method 0 for uncompressed entries.
     * 
     * @see ZipEntry#STORED
     */
    STORED(ZipEntry.STORED),

    /**
     * UnShrinking.
     * dynamic Lempel-Ziv-Welch-Algorithm
     * 
     * @see "http://www.pkware.com/documents/casestudies/APPNOTE.TXT J."
     * Explanation of fields: compression method: (2 bytes)
     */
    UNSHRINKING(1),

    /**
     * Reduced with compression factor 1.
     * 
     * @see "http://www.pkware.com/documents/casestudies/APPNOTE.TXT J."
     * Explanation of fields: compression method: (2 bytes)
     */
    EXPANDING_LEVEL_1(2),

    /**
     * Reduced with compression factor 2.
     * 
     * @see "http://www.pkware.com/documents/casestudies/APPNOTE.TXT J."
     * Explanation of fields: compression method: (2 bytes)
     */
    EXPANDING_LEVEL_2(3),

    /**
     * Reduced with compression factor 3.
     * 
     * @see "http://www.pkware.com/documents/casestudies/APPNOTE.TXT J."
     * Explanation of fields: compression method: (2 bytes)
     */
    EXPANDING_LEVEL_3(4),

    /**
     * Reduced with compression factor 4.
     * 
     * @see "http://www.pkware.com/documents/casestudies/APPNOTE.TXT J."
     * Explanation of fields: compression method: (2 bytes)
     */
    EXPANDING_LEVEL_4(5),

    /**
     * Imploding.
     * 
     * @see "http://www.pkware.com/documents/casestudies/APPNOTE.TXT J."
     * Explanation of fields: compression method: (2 bytes)
     */
    IMPLODING(6),

    /**
     * Tokenization.
     * 
     * @see "http://www.pkware.com/documents/casestudies/APPNOTE.TXT J."
     * Explanation of fields: compression method: (2 bytes)
     */
    TOKENIZATION(7),

    /**
     * Compression method 8 for compressed (deflated) entries.
     * 
     * @see ZipEntry#DEFLATED
     */
    DEFLATED(ZipEntry.DEFLATED),

    /**
     * Compression Method 9 for enhanced deflate.
     * 
     * @see "http://www.winzip.com/wz54.htm"
     */
    ENHANCED_DEFLATED(9),

    /**
     * PKWARE Data Compression Library Imploding.
     * 
     * @see "http://www.winzip.com/wz54.htm"
     */
    PKWARE_IMPLODING(10),

    /**
     * Compression Method 12 for bzip2.
     * 
     * @see "http://www.winzip.com/wz54.htm"
     */
    BZIP2(12),

    /**
     * Compression Method 14 for LZMA.
     * 
     * @see "http://www.7-zip.org/sdk.html"
     * @see "http://www.winzip.com/wz54.htm"
     */
    LZMA(14),


    /**
     * Compression Method 96 for Jpeg compression.
     * 
     * @see "http://www.winzip.com/wz54.htm"
     */
    JPEG(96),

    /**
     * Compression Method 97 for WavPack.
     * 
     * @see "http://www.winzip.com/wz54.htm"
     */
    WAVPACK(97),

    /**
     * Compression Method 98 for PPMd.
     * 
     * @see "http://www.winzip.com/wz54.htm"
     */
    PPMD(98),


    /**
     * Compression Method 99 for AES encryption.
     * 
     * @see "http://www.winzip.com/wz54.htm"
     */
    AES_ENCRYPTED(99),

    /**
     * Unknown compression method.
     */
    UNKNOWN(-1);

    private final int code;

    private static final Map<Integer, ZipMethod> codeToEnum =
        new HashMap<Integer, ZipMethod>();

    static {
        for (ZipMethod method : values()) {
            codeToEnum.put(Integer.valueOf(method.getCode()), method);
        }
    }

    /**
     * private constructor for enum style class.
     */
    ZipMethod(int code) {
        this.code = code;
    }

    /**
     * the code of the compression method.
     * 
     * @see ZipArchiveEntry#getMethod()
     * 
     * @return an integer code for the method
     */
    public int getCode() {
        return code;
    }


    /**
     * returns the {@link ZipMethod} for the given code or null if the
     * method is not known.
     */
    public static ZipMethod getMethodByCode(int code) {
        return codeToEnum.get(Integer.valueOf(code));
    }
}
