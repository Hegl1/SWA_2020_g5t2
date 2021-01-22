package at.ac.uibk.library.tests;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({
        BookmarkServiceTest.class,
        BorrowServiceTest.class,
        MediaServiceTest.class,
        UndoRedoServiceTest.class,
        UserServiceTest.class,

        EqualsImplementationTest.class,
        SessionInfoBeanTest.class,
})

public class TestSuite {

}