package org.mockitousage.bugs;

import org.junit.Test;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class ClassCastExOnVerifyZeroInteractionsTest {
    public interface TestMock {
        boolean m1();
    }

    @Test(expected = NoInteractionsWanted.class)
    public void should_not_throw_a_ClassCastException() {
        TestMock test = mock(TestMock.class, new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return false;
            }
        });
        test.m1();
        verifyZeroInteractions(test);
    }
}
