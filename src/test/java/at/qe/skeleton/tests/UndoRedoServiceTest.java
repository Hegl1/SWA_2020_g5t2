package at.qe.skeleton.tests;

import at.qe.skeleton.services.UndoRedoService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UndoRedoServiceTest {

    @Autowired
    private UndoRedoService undoRedoService;

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testCreateBookmarkAction() {

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testCreateBorrowAction() {

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testCreateMediaAction() {

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testCreateEditMediaAction() {

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testCreateUserAction() {

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testCreateEditUserAction() {

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testCreateBorrowTimesAction() {

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testAddAction() {

    }

}
