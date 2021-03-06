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
package org.apache.commons.compress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

public final class DetectArchiverTestCase extends AbstractTestCase {
    final ArchiveStreamFactory factory = new ArchiveStreamFactory();
    final ClassLoader classLoader = getClass().getClassLoader();

    public void testDetection() throws Exception {

        final ArchiveInputStream ar = getStreamFor("bla.ar"); 
        assertNotNull(ar);
        assertTrue(ar instanceof ArArchiveInputStream);

        final ArchiveInputStream tar = getStreamFor("bla.tar");
        assertNotNull(tar);
        assertTrue(tar instanceof TarArchiveInputStream);

        final ArchiveInputStream zip = getStreamFor("bla.zip");
        assertNotNull(zip);
        assertTrue(zip instanceof ZipArchiveInputStream);

        final ArchiveInputStream jar = getStreamFor("bla.jar");
        assertNotNull(jar);
        assertTrue(jar instanceof ZipArchiveInputStream);

        final ArchiveInputStream cpio = getStreamFor("bla.cpio");
        assertNotNull(cpio);
        assertTrue(cpio instanceof CpioArchiveInputStream);

// Not yet implemented        
//        final ArchiveInputStream tgz = getStreamFor("bla.tgz");
//        assertNotNull(tgz);
//        assertTrue(tgz instanceof TarArchiveInputStream);

    }

    private ArchiveInputStream getStreamFor(String resource)
            throws ArchiveException, FileNotFoundException {
        final URL rsc = classLoader.getResource(resource);
        assertNotNull("Could not find resource "+resource,rsc);
        return factory.createArchiveInputStream(
                   new BufferedInputStream(new FileInputStream(
                       new File(rsc.getFile()))));
    }
    
    // Scan list of archives in resources/archives directory
    public void notyettestArchives() throws Exception{
        File arcdir =new File(classLoader.getResource("archives").getFile());
        assertTrue(arcdir.exists());
        File[]files=arcdir.listFiles();
        for (int i=0; i<files.length; i++){
            final File file = files[i];
            if (file.getName().endsWith(".txt")){
                continue;
            }
           try {
               // TODO - how to determine expected file contents
               final ArrayList expected = new ArrayList();
               checkArchiveContent(file, expected);
            } catch (ArchiveException e) {
                fail("Problem checking "+file);
            }
        }
    }

    // Check that the empty archives created by the code are readable
    
    // Not possible to detect empty "ar" archive as it is completely empty
//    public void testEmptyArArchive() throws Exception {
//        emptyArchive("ar");
//    }

    public void testEmptyCpioArchive() throws Exception {
        checkEmptyArchive("cpio");
    }

    public void testEmptyJarArchive() throws Exception {
        checkEmptyArchive("jar");
    }

    // empty tar archives just have 512 null bytes
//    public void testEmptyTarArchive() throws Exception {
//        checkEmptyArchive("tar");
//    }
    public void testEmptyZipArchive() throws Exception {
        checkEmptyArchive("zip");
    }

    private void checkEmptyArchive(String type) throws Exception{
        File ar = createEmptyArchive(type); // will be deleted by tearDown()
        ar.deleteOnExit(); // Just in case file cannot be deleted
        ArchiveInputStream ais = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(ar));
            ais = factory.createArchiveInputStream(in);
        } catch (ArchiveException ae) {
            fail("Should have recognised empty archive for "+type);
        } finally {
            if (ais != null) {
                ais.close(); // will close input as well
            } else if (in != null){
                in.close();
            }
        }
    }
}
