/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.util;

public class SimpleMockitoLogger extends ConsoleMockitoLogger {

    StringBuilder loggedInfo = new StringBuilder();

    public void log(Object what) {
//            can be uncommented when debugging this test
//            super.log(what);
        loggedInfo.append(what);
    }

    public String getLoggedInfo() {
        return loggedInfo.toString();
    }
}