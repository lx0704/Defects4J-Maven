package org.mockitousage.bugs;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;

// issue 229 : @Mock fields in super test class are not injected on @InjectMocks fields
public class ParentTestMockInjectionTest {

    @Test
    public void injectMocksShouldInjectMocksFromTestSuperClasses() {
        ImplicitTest it = new ImplicitTest();
        MockitoAnnotations.initMocks(it);

        assertNotNull(it.daoFromParent);
        assertNotNull(it.daoFromSub);
        assertNotNull(it.sut.daoFromParent);
        assertNotNull(it.sut.daoFromSub);
    }

    @Ignore
    public static abstract class BaseTest {
        @Mock protected DaoA daoFromParent;
    }

    @Ignore("JUnit : don't this test!")
    public static class ImplicitTest extends BaseTest {
        @InjectMocks private TestedSystem sut = new TestedSystem();

        @Mock private DaoB daoFromSub;

        @Before
        public void setup() {
            MockitoAnnotations.initMocks(this);
        }

        @Test
        public void noNullPointerException() {
            sut.businessMethod();
        }
    }

    public static class TestedSystem {
        private DaoA daoFromParent;
        private DaoB daoFromSub;

        public void businessMethod() {
            daoFromParent.doQuery();
            daoFromSub.doQuery();
        }
    }


    public static class DaoA {
        public void doQuery() { }
    }

    public static class DaoB {
        public void doQuery() { }
    }

}
