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
package org.apache.commons.compress.archivers.jar;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.zip.JarMarker;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

/**
 * Subclass that adds a special extra field to the very first entry
 * which allows the created archive to be used as an executable jar on
 * Solaris.
 */
public class JarArchiveOutputStream extends ZipArchiveOutputStream {

    private boolean jarMarkerAdded = false;

    public JarArchiveOutputStream(final OutputStream out) {
        super(out);
    }

    public void putNextEntry(ZipArchiveEntry ze) throws IOException {
        if (!jarMarkerAdded) {
            ze.addAsFirstExtraField(JarMarker.getInstance());
            jarMarkerAdded = true;
        }
        super.putNextEntry(ze);
    }
}
