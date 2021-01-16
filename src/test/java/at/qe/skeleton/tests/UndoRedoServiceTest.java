package at.qe.skeleton.tests;

import at.qe.skeleton.model.*;
import at.qe.skeleton.services.*;
import at.qe.skeleton.ui.beans.ContextMocker;
import org.junit.Assert;
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

import java.util.ArrayList;
import java.util.List;
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

        Media media = this.mediaService.loadMedia(3L);
        User user = this.userService.loadUser("csauer");
        this.bookmarkService.addBookmark(user, media);
        Bookmark bookmark = this.bookmarkService.loadBookmark(11L);

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

        Media media = this.mediaService.loadMedia(12L);
        User user = this.userService.loadUser("csauer");
        this.borrowService.borrowMedia(user, media);
        Borrowed borrowed = this.borrowService.loadBorrowed(user, media);

        UndoRedoService.ActionItem borrowActionItem = this.undoRedoService.createAction(borrowed, BORROW);
        this.undoRedoService.addAction(borrowActionItem);

        Assertions.assertEquals(BORROW, this.undoRedoService.undoLastAction());
        Assertions.assertNull(this.borrowService.loadBorrowed(user, media));
        Assertions.assertEquals(BORROW, this.undoRedoService.redoLastAction());
        Assertions.assertEquals(12, this.borrowService.loadBorrowed(user, media).getBorrowID());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testUnBorrowAction() {
        // TODO: FIX

        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        Media media = this.mediaService.loadMedia(3L);
        User user = this.userService.loadUser("csauer");
        Borrowed borrowed = this.borrowService.loadBorrowed(user, media);
        this.borrowService.unBorrowMedia(borrowed);

        UndoRedoService.ActionItem unBorrowActionItem = this.undoRedoService.createAction(borrowed, UNBORROW);
        this.undoRedoService.addAction(unBorrowActionItem);

        Assertions.assertEquals(UNBORROW, this.undoRedoService.undoLastAction());
        Assertions.assertEquals(11, this.borrowService.loadBorrowed(user, media).getId());
        Assertions.assertEquals(UNBORROW, this.undoRedoService.redoLastAction());
        Assertions.assertNull(this.borrowService.loadBorrowed(user, media));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testSaveUserAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        User user = new User("csuess", "passwd", "Christian", "Süßwein", true, UserRole.CUSTOMER, "c.suesswein@swa.at");
        this.userService.saveUser(user);

        UndoRedoService.ActionItem saveUserActionItem = this.undoRedoService.createAction(user, SAVE_USER);
        this.undoRedoService.addAction(saveUserActionItem);

        Assertions.assertEquals(SAVE_USER, this.undoRedoService.undoLastAction());
        Assertions.assertNull(this.userService.loadUser("csuess"));
        Assertions.assertEquals(SAVE_USER, this.undoRedoService.redoLastAction());
        Assertions.assertEquals("csuess", this.userService.loadUser("csuess").getId());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testDeleteUserAction() throws UserService.UnauthorizedActionException {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        User user = this.userService.loadUser("customer2");
        this.userService.deleteUser(user);

        UndoRedoService.ActionItem deleteUserActionItem = this.undoRedoService.createAction(user, DELETE_USER);
        this.undoRedoService.addAction(deleteUserActionItem);

        Assertions.assertEquals(DELETE_USER, this.undoRedoService.undoLastAction());
        Assertions.assertEquals("customer2", this.userService.loadUser("customer2").getId());
        Assertions.assertEquals(DELETE_USER, this.undoRedoService.redoLastAction());
        Assertions.assertNull(this.userService.loadUser("customer2"));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testEditUserAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        User beforeEditUser = this.userService.loadUser("csauer");
        User afterEditUser = new User("csauer", "passwd", "Christian", "Süßwein", true, UserRole.CUSTOMER, "c.suesswein@swa.at");

        UndoRedoService.ActionItem editUserActionItem = this.undoRedoService.createAction(beforeEditUser, afterEditUser, EDIT_USER);
        this.undoRedoService.addAction(editUserActionItem);

        System.out.println("\n----------First step----------");
        System.out.println(beforeEditUser.getFirstName());
        System.out.println(afterEditUser.getFirstName());

        this.undoRedoService.undoLastAction();
        System.out.println("\n----------Second step----------");
        System.out.println(beforeEditUser.getFirstName());
        System.out.println(afterEditUser.getFirstName());

        this.undoRedoService.redoLastAction();
        System.out.println("\n----------Third step----------");
        System.out.println(beforeEditUser.getFirstName());
        System.out.println(afterEditUser.getFirstName());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testSaveMediaAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        Media media = new Video("Some French Video", 1789, "FR", 12, 12345);
        this.mediaService.saveMedia(media);

        UndoRedoService.ActionItem saveMediaActionItem = this.undoRedoService.createAction(media, SAVE_MEDIA);
        this.undoRedoService.addAction(saveMediaActionItem);

        Assertions.assertEquals(SAVE_MEDIA, this.undoRedoService.undoLastAction());
        Assertions.assertNull(this.mediaService.loadMedia(21L));
        Assertions.assertEquals(SAVE_MEDIA, this.undoRedoService.redoLastAction());
        Assertions.assertEquals(22, this.mediaService.loadMedia(22L).getMediaID());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testDeleteMediaAction() {
        // TODO: FIX

        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        Media media = this.mediaService.loadMedia(13L);
        this.mediaService.deleteMedia(media);

        UndoRedoService.ActionItem deleteMediaActionItem = this.undoRedoService.createAction(media, DELETE_MEDIA);
        this.undoRedoService.addAction(deleteMediaActionItem);

        Assertions.assertEquals(DELETE_MEDIA, this.undoRedoService.undoLastAction());
        Assertions.assertEquals(13, this.mediaService.loadMedia(13L).getMediaID());
        Assertions.assertEquals(DELETE_MEDIA, this.undoRedoService.redoLastAction());
        Assertions.assertNull(this.mediaService.loadMedia(13L));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testEditMediaAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();


    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testEditBorrowTimesAction() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();


    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "amuss", authorities = { "ADMIN" })
    public void testAddActionWithTooManyActions() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

    }

}
