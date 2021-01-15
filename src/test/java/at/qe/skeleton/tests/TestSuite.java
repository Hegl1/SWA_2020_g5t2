package at.qe.skeleton.tests;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({
        at.qe.skeleton.tests.MediaServiceTest.class,
        at.qe.skeleton.tests.UserServiceTest.class,
        at.qe.skeleton.tests.BorrowServiceTest.class,
        BookmarkTest.class,
        at.qe.skeleton.tests.EqualsImplementationTest.class,
        at.qe.skeleton.tests.SessionInfoBeanTest.class,
})

public class TestSuite {

}