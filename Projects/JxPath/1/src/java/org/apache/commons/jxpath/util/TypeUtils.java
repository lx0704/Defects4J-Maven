/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.util;

/**
 * Global type conversion utilities.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class TypeUtils {
    private static TypeConverter typeConverter = new BasicTypeConverter();

    /**
     * Install an alternative type converter.
     */
    public static synchronized void setTypeConverter(TypeConverter converter) {
        typeConverter = converter;
    }

    /**
     * Returns the current type converter.
     */
    public static TypeConverter getTypeConverter() {
        return typeConverter;
    }

    /**
     * Returns true if the global converter can convert the supplied
     * object to the specified type.
     */
    public static boolean canConvert(Object object, Class toType) {
        return typeConverter.canConvert(object, toType);
    }

    /**
     * Converts the supplied object to the specified type. May
     * throw a RuntimeException.
     */
    public static Object convert(Object object, Class toType) {
        return typeConverter.convert(object, toType);
    }
}