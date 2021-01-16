package at.qe.skeleton.tests;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.*;
import at.qe.skeleton.ui.beans.ContextMocker;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.faces.context.FacesContext;

import java.util.NoSuchElementException;

import static at.qe.skeleton.services.UndoRedoService.ActionType.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UndoRedoServiceTest {

    @Autowired
    private UndoRedoService undoRedoService;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testSaveBookmarkAction() {
        // TODO: FIX

        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        Media media = this.mediaService.loadMedia(2L);
        User user = this.userService.loadUser("csauer");
        Bookmark bookmark = this.bookmarkService.getBookmarkByUserAndMedia(user, media);

        UndoRedoService.ActionItem saveBookmarkActionItem = this.undoRedoService.createAction(bookmark, SAVE_BOOKMARK);
        this.undoRedoService.addAction(saveBookmarkActionItem);

        Assertions.assertEquals(SAVE_BOOKMARK, this.undoRedoService.undoLastAction());
        Assertions.assertNull(this.bookmarkService.getBookmarkByUserAndMedia(user, media));
        Assertions.assertEquals(SAVE_BOOKMARK, this.undoRedoService.redoLastAction());
        Assertions.assertEquals(11, this.bookmarkService.getBookmarkByUserAndMedia(user, media).getBookmarkID());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testDeleteBookmarkAction() {
        // TODO: FIX

        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        Media media = this.mediaService.loadMedia(2L);
        User user = this.userService.loadUser("csauer");
        Bookmark bookmark = this.bookmarkService.getBookmarkByUserAndMedia(user, media);

        UndoRedoService.ActionItem deleteBookmarkActionItem = this.undoRedoService.createAction(bookmark, DELETE_BOOKMARK);
        this.undoRedoService.addAction(deleteBookmarkActionItem);

        Assertions.assertEquals(DELETE_BOOKMARK, this.undoRedoService.undoLastAction());
        Assertions.assertEquals(1, this.bookmarkService.getBookmarkByUserAndMedia(user, media).getId());
        Assertions.assertEquals(DELETE_BOOKMARK, this.undoRedoService.redoLastAction());
        Assertions.assertNull(this.bookmarkService.getBookmarkByUserAndMedia(user, media));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testBorrowAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        Media media = this.mediaService.loadMedia(3L);
        User user = this.userService.loadUser("csauer");
        Borrowed borrowed = this.borrowService.loadBorrowed(user, media);

        UndoRedoService.ActionItem borrowActionItem = this.undoRedoService.createAction(borrowed, BORROW);
        this.undoRedoService.addAction(borrowActionItem);

        Assertions.assertEquals(BORROW, this.undoRedoService.undoLastAction());
        Assertions.assertNull(this.borrowService.loadBorrowed(user, media));
        Assertions.assertEquals(BORROW, this.undoRedoService.redoLastAction());
        Assertions.assertEquals(11, this.borrowService.loadBorrowed(user, media).getBorrowID());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testUnBorrowAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        Media media = this.mediaService.loadMedia(3L);
        User user = this.userService.loadUser("csauer");
        Borrowed borrowed = this.borrowService.loadBorrowed(user, media);

        UndoRedoService.ActionItem unBorrowActionItem = this.undoRedoService.createAction(borrowed, UNBORROW);
        this.undoRedoService.addAction(unBorrowActionItem);

        Assertions.assertEquals(UNBORROW, this.undoRedoService.undoLastAction());
        Assertions.assertEquals(1, this.borrowService.loadBorrowed(user, media).getId());
        Assertions.assertEquals(UNBORROW, this.undoRedoService.redoLastAction());
        Assertions.assertNull(this.borrowService.loadBorrowed(user, media));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testSaveUserAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        User user = this.userService.loadUser("csauer");

        UndoRedoService.ActionItem saveUserActionItem = this.undoRedoService.createAction(user, SAVE_USER);
        this.undoRedoService.addAction(saveUserActionItem);

        Assertions.assertEquals(SAVE_USER, this.undoRedoService.undoLastAction());
        Assertions.assertEquals(SAVE_USER, this.undoRedoService.redoLastAction());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testDeleteUserAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        User user = this.userService.loadUser("csauer");

        UndoRedoService.ActionItem deleteUserActionItem = this.undoRedoService.createAction(user, DELETE_USER);
        this.undoRedoService.addAction(deleteUserActionItem);

        Assertions.assertEquals(DELETE_USER, this.undoRedoService.undoLastAction());
        Assertions.assertEquals(DELETE_USER, this.undoRedoService.redoLastAction());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testEditUserAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();


    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testSaveMediaAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        Media media = this.mediaService.loadMedia(13L);

        UndoRedoService.ActionItem saveMediaActionItem = this.undoRedoService.createAction(media, SAVE_MEDIA);
        this.undoRedoService.addAction(saveMediaActionItem);

        Assertions.assertEquals(SAVE_MEDIA, this.undoRedoService.undoLastAction());
        Assertions.assertEquals(SAVE_MEDIA, this.undoRedoService.redoLastAction());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testDeleteMediaAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        Media media = this.mediaService.loadMedia(13L);

        UndoRedoService.ActionItem deleteMediaActionItem = this.undoRedoService.createAction(media, DELETE_MEDIA);
        this.undoRedoService.addAction(deleteMediaActionItem);

        Assertions.assertEquals(DELETE_MEDIA, this.undoRedoService.undoLastAction());
        Assertions.assertEquals(DELETE_MEDIA, this.undoRedoService.redoLastAction());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testEditMediaAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();


    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testEditBorrowTimesAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();


    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testAddAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();


    }

}
