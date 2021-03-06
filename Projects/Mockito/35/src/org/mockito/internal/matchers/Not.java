/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;

@SuppressWarnings("unchecked")
public class Not extends ArgumentMatcher {

    private static final long serialVersionUID = 4627373642333593264L;
    private final Matcher first;

    public Not(Matcher first) {
        this.first = first;
    }

    public boolean matches(Object actual) {
        return !first.matches(actual);
    }

    public void describeTo(Description description) {
        description.appendText("not(");
        first.describeTo(description);
        description.appendText(")");
    }
}