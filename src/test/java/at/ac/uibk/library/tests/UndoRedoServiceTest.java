package at.ac.uibk.library.tests;

import at.ac.uibk.library.model.*;
import at.ac.uibk.library.repositories.MediaBorrowTimeRepository;
import at.ac.uibk.library.services.*;
import at.ac.uibk.library.ui.beans.ContextMocker;
import at.ac.uibk.library.utils.UnallowedInputException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.faces.context.FacesContext;
import java.util.Collection;

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

	@Autowired
	private MediaBorrowTimeRepository mediaBorrowTimeRepository;

	@Test
	@DirtiesContext
	@WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
	public void testIsUndoActionAvailable() {
		Assertions.assertFalse(this.undoRedoService.isUndoActionAvailable());

		Bookmark bookmark = this.bookmarkService.loadBookmark(11L);
		UndoRedoService.ActionItem saveBookmarkActionItem = this.undoRedoService.createAction(bookmark, UndoRedoService.ActionType.SAVE_BOOKMARK);
		this.undoRedoService.addAction(saveBookmarkActionItem);

		Assertions.assertTrue(this.undoRedoService.isUndoActionAvailable());
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
	public void testIsRedoActionAvailable()
			throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		Assertions.assertFalse(this.undoRedoService.isRedoActionAvailable());

		Bookmark bookmark = this.bookmarkService.loadBookmark(11L);
		UndoRedoService.ActionItem saveBookmarkActionItem = this.undoRedoService.createAction(bookmark, UndoRedoService.ActionType.SAVE_BOOKMARK);
		this.undoRedoService.addAction(saveBookmarkActionItem);
		this.undoRedoService.undoLastAction();

		Assertions.assertTrue(this.undoRedoService.isRedoActionAvailable());
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
	public void testSaveBookmarkAction()
			throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		Media media = this.mediaService.loadMedia(3L);
		User user = this.userService.getAuthenticatedUser();
		this.bookmarkService.addBookmark(user, media);
		Bookmark bookmark = this.bookmarkService.loadBookmark(11L);

		UndoRedoService.ActionItem saveBookmarkActionItem = this.undoRedoService.createAction(bookmark, UndoRedoService.ActionType.SAVE_BOOKMARK);
		this.undoRedoService.addAction(saveBookmarkActionItem);

		Assertions.assertNotNull(this.bookmarkService.getBookmarkByUserAndMedia(user, media));

		this.undoRedoService.undoLastAction();
		Assertions.assertNull(this.bookmarkService.getBookmarkByUserAndMedia(user, media));

		this.undoRedoService.redoLastAction();
		Assertions.assertEquals(12, this.bookmarkService.getBookmarkByUserAndMedia(user, media).getBookmarkID());
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testDeleteBookmarkAction()
			throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		Media media = this.mediaService.loadMedia(2L);
		User user = this.userService.loadUser("csauer");
		Bookmark bookmark = this.bookmarkService.getBookmarkByUserAndMedia(user, media);

		UndoRedoService.ActionItem deleteBookmarkActionItem = this.undoRedoService.createAction(bookmark,
				UndoRedoService.ActionType.DELETE_BOOKMARK);
		this.undoRedoService.addAction(deleteBookmarkActionItem);

		this.undoRedoService.undoLastAction();
		Assertions.assertEquals(1, this.bookmarkService.getBookmarkByUserAndMedia(user, media).getId());
		this.undoRedoService.redoLastAction();
		Assertions.assertNull(this.bookmarkService.getBookmarkByUserAndMedia(user, media));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testBorrowAction() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		Media media = this.mediaService.loadMedia(12L);
		User user = this.userService.loadUser("csauer");
		this.borrowService.borrowMedia(user, media);
		Borrowed borrowed = this.borrowService.loadBorrowed(user, media);

		UndoRedoService.ActionItem borrowActionItem = this.undoRedoService.createAction(borrowed, UndoRedoService.ActionType.BORROW);
		this.undoRedoService.addAction(borrowActionItem);

		this.undoRedoService.undoLastAction();
		Assertions.assertNull(this.borrowService.loadBorrowed(user, media));
		this.undoRedoService.redoLastAction();
		Assertions.assertEquals(12, this.borrowService.loadBorrowed(user, media).getBorrowID());
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testUnBorrowAction() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		Media media = this.mediaService.loadMedia(3L);
		User user = this.userService.loadUser("csauer");
		Borrowed borrowed = this.borrowService.loadBorrowed(user, media);

		this.borrowService.unBorrowMedia(borrowed);

		UndoRedoService.ActionItem unBorrowActionItem = this.undoRedoService.createAction(borrowed, UndoRedoService.ActionType.UNBORROW);
		this.undoRedoService.addAction(unBorrowActionItem);

		this.undoRedoService.undoLastAction();
		Assertions.assertEquals(11, this.borrowService.loadBorrowed(user, media).getId());

		this.undoRedoService.redoLastAction();
		Assertions.assertNull(this.borrowService.loadBorrowed(user, media));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testSaveUserAction() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		User user = new User("csuess", "passwd", "Christian", "Süßwein", true, UserRole.CUSTOMER, "c.suesswein@swa.at");
		this.userService.saveUser(user);

		UndoRedoService.ActionItem saveUserActionItem = this.undoRedoService.createAction(user, UndoRedoService.ActionType.SAVE_USER);
		this.undoRedoService.addAction(saveUserActionItem);

		this.undoRedoService.undoLastAction();
		Assertions.assertNull(this.userService.loadUser("csuess"));
		this.undoRedoService.redoLastAction();
		Assertions.assertEquals("csuess", this.userService.loadUser("csuess").getId());
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testDeleteUserAction() throws UserService.UnauthorizedActionException,
			MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		User user = this.userService.loadUser("customer2");
		this.userService.deleteUser(user);

		UndoRedoService.ActionItem deleteUserActionItem = this.undoRedoService.createAction(user, UndoRedoService.ActionType.DELETE_USER);
		this.undoRedoService.addAction(deleteUserActionItem);

		this.undoRedoService.undoLastAction();
		Assertions.assertEquals("customer2", this.userService.loadUser("customer2").getId());
		this.undoRedoService.redoLastAction();
		Assertions.assertNull(this.userService.loadUser("customer2"));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testEditUserAction() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		User beforeEditUser = this.userService.loadUser("csauer");
		User afterEditUser = new User("csauer", "passwd", "Christian", "Süßwein", true, UserRole.CUSTOMER,
				"c.suesswein@swa.at");

		this.userService.saveUser(afterEditUser);
		User user = this.userService.loadUser("csauer");

		UndoRedoService.ActionItem editUserActionItem = this.undoRedoService.createAction(beforeEditUser, afterEditUser,
				UndoRedoService.ActionType.EDIT_USER);
		this.undoRedoService.addAction(editUserActionItem);

		Assertions.assertEquals("Christian", user.getFirstName());

		this.undoRedoService.undoLastAction();
		user = this.userService.loadUser("csauer");
		Assertions.assertEquals("Clemens", user.getFirstName());

		this.undoRedoService.redoLastAction();
		user = this.userService.loadUser("csauer");
		Assertions.assertEquals("Christian", user.getFirstName());
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testSaveMediaAction() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		Media media = new Video("Some French Video", 1789, "FR", 12, 12345);
		this.mediaService.saveMedia(media);

		UndoRedoService.ActionItem saveMediaActionItem = this.undoRedoService.createAction(media, UndoRedoService.ActionType.SAVE_MEDIA);
		this.undoRedoService.addAction(saveMediaActionItem);

		this.undoRedoService.undoLastAction();
		Assertions.assertNull(this.mediaService.loadMedia(21L));
		this.undoRedoService.redoLastAction();
		Assertions.assertEquals(22, this.mediaService.loadMedia(22L).getMediaID());
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testDeleteMediaAction()
			throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		Media media = this.mediaService.loadMedia(13L);
		this.mediaService.deleteMedia(media);

		UndoRedoService.ActionItem deleteMediaActionItem = this.undoRedoService.createAction(media, UndoRedoService.ActionType.DELETE_MEDIA);
		this.undoRedoService.addAction(deleteMediaActionItem);

		this.undoRedoService.undoLastAction();
		Assertions.assertEquals(21, this.mediaService.loadMedia(21L).getMediaID());
		this.undoRedoService.redoLastAction();
		Assertions.assertNull(this.mediaService.loadMedia(13L));
		Assertions.assertNull(this.mediaService.loadMedia(21L));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testEditMediaAction() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		Media beforeEditMedia = this.mediaService.loadMedia(20L);
		Media afterEditMedia = this.mediaService.loadMedia(20L);

		afterEditMedia.setTitle("Some French Book");
		afterEditMedia.setLanguage("FR");
		this.mediaService.saveMedia(afterEditMedia);

		Media media = this.mediaService.loadMedia(20L);

		UndoRedoService.ActionItem editMediaActionItem = this.undoRedoService.createAction(beforeEditMedia,
				afterEditMedia, UndoRedoService.ActionType.EDIT_MEDIA);
		this.undoRedoService.addAction(editMediaActionItem);

		Assertions.assertEquals(afterEditMedia, media);

		this.undoRedoService.undoLastAction();
		media = this.mediaService.loadMedia(20L);
		Assertions.assertEquals(beforeEditMedia, media);

		this.undoRedoService.redoLastAction();
		media = this.mediaService.loadMedia(20L);
		Assertions.assertEquals(afterEditMedia, media);
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testEditBorrowTimesAction()
			throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		Collection<MediaBorrowTime> mediaBorrowTimes = this.mediaBorrowTimeRepository.findAll();
		Collection<MediaBorrowTime> newMediaBorrowTimes = this.mediaBorrowTimeRepository.findAll();
		// Assertions.assertTrue(checkInitialBorrowTimes(mediaBorrowTimes));

		UndoRedoService.ActionItem borrowTimeAction = this.undoRedoService.createAction(mediaBorrowTimes,
				UndoRedoService.ActionType.EDIT_MEDIA_BORROW_TIME);
		this.undoRedoService.addAction(borrowTimeAction);

		for (MediaBorrowTime current : newMediaBorrowTimes) {
			current.setAllowedBorrowTime(69);
			this.mediaBorrowTimeRepository.save(current);
		}

		Assertions.assertTrue(
				newMediaBorrowTimes.stream().allMatch(mediaBorrowTime -> mediaBorrowTime.getAllowedBorrowTime() == 69));

		this.undoRedoService.undoLastAction();
		newMediaBorrowTimes = this.mediaBorrowTimeRepository.findAll();
		Assertions.assertTrue(checkInitialBorrowTimes(newMediaBorrowTimes));

		this.undoRedoService.redoLastAction();
		newMediaBorrowTimes = this.mediaBorrowTimeRepository.findAll();
		Assertions.assertTrue(
				newMediaBorrowTimes.stream().allMatch(mediaBorrowTime -> mediaBorrowTime.getAllowedBorrowTime() == 69));
	}

	private boolean checkInitialBorrowTimes(final Collection<MediaBorrowTime> mediaBorrowTimes) {

		int i = 0;

		for (MediaBorrowTime current : mediaBorrowTimes) {
			if (i == 0 && !(current.getAllowedBorrowTime() == 7)) {
				return false;
			}
			if (i == 1 && !(current.getAllowedBorrowTime() == 21)) {
				return false;
			}
			if ((i == 2 || i == 3) && !(current.getAllowedBorrowTime() == 14)) {
				return false;
			}
			i++;
		}
		return true;

	}

}
