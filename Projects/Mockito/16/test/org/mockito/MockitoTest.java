/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockitoutil.TestBase;

@SuppressWarnings("unchecked")
public class MockitoTest extends TestBase {

    @Test
    public void shouldRemoveStubbableFromProgressAfterStubbing() {
        List mock = Mockito.mock(List.class);
        Mockito.when(mock.add("test")).thenReturn(true);
        //FIXME Consider to move to separate test
        assertNull(new ThreadSafeMockingProgress().pullOngoingStubbing());
    }
    
    @Test(expected=NotAMockException.class)
    public void shouldValidateMockWhenVerifying() {
        Mockito.verify("notMock");
    }
    
    @Test(expected=NotAMockException.class)
    public void shouldValidateMockWhenVerifyingWithExpectedNumberOfInvocations() {
        Mockito.verify("notMock", times(19));
    }
    
    @Test(expected=NotAMockException.class)
    public void shouldValidateMockWhenVerifyingNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions("notMock");
    }
    
    @Test(expected=NotAMockException.class)
    public void shouldValidateMockWhenVerifyingZeroInteractions() {
        Mockito.verifyZeroInteractions("notMock");
    }
    
    @SuppressWarnings("deprecation")
    @Test(expected=NotAMockException.class)
    public void shouldValidateMockWhenStubbingVoid() {
        Mockito.stubVoid("notMock");
    }
    
    @Test(expected=NotAMockException.class)
    public void shouldValidateMockWhenCreatingInOrderObject() {
        Mockito.inOrder("notMock");
    }
    
    @Test
    public void shouldStartingMockSettingsContainDefaultBehavior() {
        //when
        MockSettingsImpl settings = (MockSettingsImpl) Mockito.withSettings();
        
        //then
        assertEquals(Mockito.RETURNS_DEFAULTS, settings.getDefaultAnswer());
    }
    
    //TODO: after 1.8 stack filter does not work very well when it comes to threads?
}