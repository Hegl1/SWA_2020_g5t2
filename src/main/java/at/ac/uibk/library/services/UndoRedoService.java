package at.ac.uibk.library.services;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ac.uibk.library.model.Bookmark;
import at.ac.uibk.library.model.Borrowed;
import at.ac.uibk.library.model.Media;
import at.ac.uibk.library.model.MediaBorrowTime;
import at.ac.uibk.library.model.Reserved;
import at.ac.uibk.library.model.User;
import at.ac.uibk.library.model.UserRole;
import at.ac.uibk.library.repositories.MediaBorrowTimeRepository;
import at.ac.uibk.library.services.UserService.UnauthorizedActionException;
import at.ac.uibk.library.utils.UnallowedInputException;

/**
 * Class that provides a Service to undo and redo certain actions. As the class
 * follows the command pattern, there are several inner classes that represent
 * commands. For a enumeration of provided actions, see
 * {@link UndoRedoService.ActionType} The abstract class
 * for an action is {@link UndoRedoService.ActionItem}.
 * These classes will be instantiated with the provided methods. Note that it is
 * necessary to add all actions that should be 'undoable' with the method
 * {@link UndoRedoService#addAction(ActionItem)}. Redoable actions are managed
 * automatically.
 * 
 * @author Marcel Huber
 * @version 1.0
 *
 */

@Component
@Scope("session")
public class UndoRedoService {

	/**
	 * Double ended Queue that holds ActionItems to undo. Is basically used as a
	 * stack.
	 */
	private final Deque<ActionItem> unDoQueue;

	/**
	 * Double ended Queue that holds ActionItems to redo. Is basically used as a
	 * stack.
	 */
	private final Deque<ActionItem> reDoQueue;

	@Autowired
	private UserService userService;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private BorrowService borrowService;

	@Autowired
	private BookmarkService bookmarkService;

	@Autowired
	private MediaBorrowTimeRepository mediaBorrowTimeRepository;

	private final Logger logger = LoggerFactory.getLogger(UndoRedoService.class);

	/**
	 * Constant that represents the number of maximum states that can be undone.
	 */
	private static final int MAX_SAVED_STATES = 20;

	/**
	 * Default constructor for UndoRedoService. Instantiates dequeues for undoing
	 * and redoing.
	 */
	@Autowired
	public UndoRedoService() {
		unDoQueue = new ArrayDeque<>(MAX_SAVED_STATES + 1);
		reDoQueue = new ArrayDeque<>(MAX_SAVED_STATES + 1);
	}

	/**
	 * Method that adds an action to the undoing queue. The redo-queue will be
	 * emptied.
	 *
	 * @param action the action that should be prepared for undoing.
	 */
	public void addAction(final ActionItem action) {
		unDoQueue.push(action);
		if (unDoQueue.size() >= MAX_SAVED_STATES) {
			unDoQueue.removeLast();
		}

		reDoQueue.clear();
	}

	/**
	 * Method that undoes the last saved action.
	 *
	 * @return the undid action-type
	 */
	public ActionType undoLastAction()
			throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		ActionItem action = unDoQueue.pop();
		if (action != null) {
			action.performUndoAction();
			reDoQueue.push(action);
			if (reDoQueue.size() >= MAX_SAVED_STATES) {
				reDoQueue.removeLast();
			}

			return action.type;
		}

		return null;
	}

	/**
	 * Method that redoes the last action.
	 *
	 * @return the redid action-type
	 */
	public ActionType redoLastAction()
			throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		ActionItem action = reDoQueue.pop();
		if (action != null) {
			action.performRedoAction();

			return action.type;
		}

		return null;
	}

	/**
	 * Method that checks whether an undo action is available.
	 * 
	 * @return true if an action is available, else false.
	 */
	public boolean isUndoActionAvailable() {
		return !unDoQueue.isEmpty();
	}

	/**
	 * Method that checks whether an redo action is available.
	 * 
	 * @return true if an action is available, else false.
	 */
	public boolean isRedoActionAvailable() {
		return !reDoQueue.isEmpty();
	}

	/**
	 * Method that creates an ActionItem.
	 * 
	 * @param borrow the Borrowed which is used in the action,
	 * @param type   the type that is performed in the action. Note that only
	 *               {@link UndoRedoService.ActionType#BORROW}
	 *               and
	 *               {@link UndoRedoService.ActionType#UNBORROW}
	 *               are possible values for Borrows.
	 * @return the constructed ActionItem.
	 */
	public ActionItem createAction(final Borrowed borrow, final ActionType type) {
		return new BorrowAction(borrow, type);
	}

	/**
	 * Method that creates an ActionItem.
	 * 
	 * @param bookmark the Bookmark which is used in the action,
	 * @param type     the type that is performed in the action. Note that only
	 *                 {@link UndoRedoService.ActionType#SAVE_BOOKMARK}
	 *                 and
	 *                 {@link UndoRedoService.ActionType#DELETE_BOOKMARK}
	 *                 are possible values for Bookmarks.
	 * @return the constructed ActionItem.
	 */
	public ActionItem createAction(final Bookmark bookmark, final ActionType type) {
		return new BookmarkAction(bookmark, type);
	}

	/**
	 * Method that creates an ActionItem. Not possible to use for the EDIT_USER
	 * action, instead use
	 * {@link UndoRedoService#createAction(User, User, ActionType)}
	 * 
	 * @param user the user which is used in the action,
	 * @param type the type that is performed in the action. Note that only
	 *             {@link UndoRedoService.ActionType#SAVE_USER},
	 *             and
	 *             {@link UndoRedoService.ActionType#DELETE_USER}
	 *             are possible values for this method.
	 * @return the constructed ActionItem or null if the wrong action type is used.
	 */
	public ActionItem createAction(final User user, final ActionType type) {
		if (type.equals(ActionType.EDIT_USER)) {
			logger.error("Action could not be saved for user " + user.getUsername()
					+ " - wrong action type in wrong method");
			return null;
		} else {
			return new UserAction(user, type);
		}
	}

	/**
	 * Method that creates an ActionItem. Not possible to use for SAVE_USER and
	 * DELETE_USER actions, instead use
	 * {@link UndoRedoService#createAction(User, ActionType)}
	 * 
	 * @param beforeEditUser the user before editing has been performed
	 * @param afterEditUser  the user after editing has been performed
	 * @param type           the type that is performed in the action. Note that
	 *                       only
	 *                       {@link UndoRedoService.ActionType#EDIT_USER},
	 *                       is possible for this method.
	 * @return the constructed ActionItem or null if the wrong action type is used.
	 */
	public ActionItem createAction(final User beforeEditUser, final User afterEditUser, final ActionType type) {
		if (!type.equals(ActionType.EDIT_USER)) {
			logger.error("Action could not be saved for user " + afterEditUser.getUsername()
					+ " - wrong action type in wrong method");
			return null;
		} else {
			return new UserAction(beforeEditUser, afterEditUser, type);
		}

	}

	/**
	 * Method that creates an ActionItem. Not recommended to use for the EDIT_MEDIA
	 * action, instead use
	 * {@link UndoRedoService#createAction(Media, Media, ActionType)}
	 * 
	 * @param media the media which is used in the action,
	 * @param type  the type that is performed in the action. Note that only
	 *              {@link UndoRedoService.ActionType#SAVE_MEDIA},
	 *              and
	 *              {@link UndoRedoService.ActionType#DELETE_MEDIA}
	 *              are possible values for this method.
	 * @return the constructed ActionItem or null if the wrong action type is used.
	 */
	public ActionItem createAction(final Media media, final ActionType type) {
		if (type.equals(ActionType.EDIT_MEDIA)) {
			logger.error(
					"Action could not be saved for media " + media.getId() + " - wrong action type in wrong method");
			return null;
		}
		return new MediaAction(media, type);
	}

	/**
	 * Method that creates an ActionItem. Not possible to use for SAVE_MEDIA and
	 * DELETE_MEDIA actions, instead use
	 * {@link UndoRedoService#createAction(Media, ActionType)}
	 * 
	 * @param beforeEditMedia the media before editing has been performed
	 * @param afterEditMedia  the media after editing has been performed
	 * @param type            the type that is performed in the action. Note that
	 *                        only
	 *                        {@link UndoRedoService.ActionType#EDIT_MEDIA},
	 *                        is possible for this method.
	 * @return the constructed ActionItem or null if the wrong action type is used.
	 */
	public ActionItem createAction(final Media beforeEditMedia, final Media afterEditMedia, final ActionType type) {
		if (!type.equals(ActionType.EDIT_MEDIA)) {
			logger.error("Action could not be saved for media " + afterEditMedia.getId()
					+ " - wrong action type in wrong method");
			return null;
		}
		return new MediaAction(beforeEditMedia, afterEditMedia, type);
	}

	public ActionItem createAction(final Collection<MediaBorrowTime> borrowTimes, final ActionType type) {
		if (!type.equals(ActionType.EDIT_MEDIA_BORROW_TIME)) {
			logger.error("Action could not be saved for MediaBorrowTimes: wrong action type in wrong method.");
		}
		return new BorrowTimeAction(borrowTimes, type);
	}

	/**
	 * Abstract class that represents an Action. Contains abstract methods for
	 * undoing and redoing the saved action.
	 *
	 */
	public abstract static class ActionItem {

		/**
		 * Type that is used to choose the correct counter action
		 */
		protected ActionType type;

		/**
		 * method that undoes the recent action.
		 */
		abstract void performUndoAction()
				throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException;

		/**
		 * method that redoes the recent action.
		 */
		abstract void performRedoAction()
				throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException;
	}

	/**
	 * Class that represents a borrowing action.
	 */
	private class BorrowAction extends ActionItem {

		/**
		 * Borrowed that is used for undoing/redoing
		 */
		protected Borrowed borrowed;

		protected BorrowAction(final Borrowed borrowed, final ActionType type) {
			this.borrowed = borrowed;
			this.type = type;
		}

		@Override
		protected void performUndoAction() {
			if (type.equals(ActionType.BORROW)) {
				recacheMediaForBorrowed();

				if (!userService.loadCurrentUser().getRoles().contains(UserRole.CUSTOMER)) {
					borrowService.unBorrowMedia(borrowed);
				} else {
					borrowService.unBorrowMediaForAuthenticatedUser(borrowed.getMedia());
				}
			} else if (type.equals(ActionType.UNBORROW)) {
				recacheMediaForBorrowed();

				if (!userService.loadCurrentUser().getRoles().contains(UserRole.CUSTOMER)) {
					borrowService.borrowMediaOnDate(borrowed.getUser(), borrowed.getMedia(), borrowed.getBorrowDate());
				} else {
					borrowService.borrowMediaForAuthenticatedUserOnDate(borrowed.getMedia(), borrowed.getBorrowDate());
				}
			} else {
				logger.error("Error while undoing borrow action, wrong action type");
			}
		}

		@Override
		protected void performRedoAction() {
			if (type.equals(ActionType.BORROW)) {
				recacheMediaForBorrowed();

				if (!userService.loadCurrentUser().getRoles().contains(UserRole.CUSTOMER)) {
					borrowService.borrowMediaOnDate(borrowed.getUser(), borrowed.getMedia(), borrowed.getBorrowDate());
				} else {
					borrowService.borrowMediaForAuthenticatedUserOnDate(borrowed.getMedia(), borrowed.getBorrowDate());
				}
			} else if (type.equals(ActionType.UNBORROW)) {
				recacheMediaForBorrowed();

				if (!userService.loadCurrentUser().getRoles().contains(UserRole.CUSTOMER)) {
					borrowService.unBorrowMedia(borrowed.getUser(), borrowed.getMedia());
				} else {
					borrowService.unBorrowMediaForAuthenticatedUser(borrowed.getMedia());
				}
			} else {
				logger.error("Error while redoing borrow action, wrong action type");
			}
		}

		private void recacheMediaForBorrowed() {
			this.borrowed.setMedia(mediaService.loadMedia(this.borrowed.getMedia().getMediaID()));
		}
	}

	/**
	 * Class that represents a bookmarking action.
	 */
	private class BookmarkAction extends ActionItem {
		/**
		 * Bookmark that is used for undoing/redoing
		 */
		protected Bookmark bookmark;

		protected BookmarkAction(final Bookmark bookmark, final ActionType type) {
			this.bookmark = bookmark;
			this.type = type;
		}

		@Override
		protected void performUndoAction() {
			if (type.equals(ActionType.SAVE_BOOKMARK)) {
				bookmarkService.deleteBookmark(bookmark);
			} else if (type.equals(ActionType.DELETE_BOOKMARK)) {
				bookmarkService.addBookmark(bookmark.getMedia());
			} else {
				logger.error("Error while undoing bookmark action, wrong action type");
			}
		}

		@Override
		protected void performRedoAction() {
			if (type.equals(ActionType.SAVE_BOOKMARK)) {
				bookmarkService.addBookmark(bookmark.getMedia());
			} else if (type.equals(ActionType.DELETE_BOOKMARK)) {
				bookmarkService.deleteBookmark(
						bookmarkService.getBookmarkByUserAndMedia(bookmark.getUser(), bookmark.getMedia()));
			} else {
				logger.error("Error while redoing bookmark action, wrong action type");
			}
		}

	}

	/**
	 * Class that represents a user action.
	 */
	private class UserAction extends ActionItem {

		/**
		 * User that is used for un/redoing every action.
		 */
		protected User beforeEditUser;

		/**
		 * User that is only used for edit actions.
		 */
		protected User afterEditUser;

		protected UserAction(final User user, final ActionType type) {
			this.beforeEditUser = user;
			this.type = type;
			this.afterEditUser = null;
		}

		protected UserAction(final User user, final User afterEditUser, final ActionType type) {
			this(user, type);
			this.afterEditUser = afterEditUser;
		}

		@Override
		void performUndoAction() throws UnallowedInputException {
			if (type.equals(ActionType.SAVE_USER)) {
				try {
					userService.deleteUser(beforeEditUser);
				} catch (UnauthorizedActionException e) {
					logger.error("Error while undoing user action unauthorized deletion of user");
				}
			} else if (type.equals(ActionType.DELETE_USER)) {
				userService.saveUser(beforeEditUser);
			} else if (type.equals(ActionType.EDIT_USER)) {
				userService.saveUser(beforeEditUser);
			} else {
				logger.error("Error while undoing user action, wrong action type");
			}

		}

		@Override
		void performRedoAction() throws UnallowedInputException {
			if (type.equals(ActionType.SAVE_USER)) {
				userService.saveUser(beforeEditUser);
			} else if (type.equals(ActionType.DELETE_USER)) {
				try {
					userService.deleteUser(beforeEditUser);
				} catch (UnauthorizedActionException e) {
					logger.error("Error while undoing user action unauthorized deletion of user");
				}
			} else if (type.equals(ActionType.EDIT_USER)) {
				userService.saveUser(afterEditUser);
			} else {
				logger.error("Error while redoing user action, wrong action type");
			}

		}

	}

	/**
	 * Class that represents a media action.
	 */
	private class MediaAction extends ActionItem {
		/**
		 * Media that is used for un/redoing every action.
		 */
		protected Media beforeEditMedia;

		/**
		 * Media that is only used for edit actions.
		 */
		protected Media afterEditMedia;

		protected Collection<Reserved> reserveList;

		protected Collection<Bookmark> bookMarkList;

		protected MediaAction(final Media media, final ActionType type) {
			this.beforeEditMedia = media;
			this.type = type;
			this.afterEditMedia = null;
			if (type.equals(ActionType.DELETE_MEDIA)) {
				saveMetaInfo();
			}
		}

		protected MediaAction(final Media beforeEditMedia, final Media afterEditMedia, final ActionType type) {
			this(beforeEditMedia, type);
			this.afterEditMedia = afterEditMedia;
		}

		@Override
		void performUndoAction() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
			if (type.equals(ActionType.SAVE_MEDIA)) {
				saveMetaInfo();
				mediaService.deleteMedia(beforeEditMedia);
			} else if (type.equals(ActionType.DELETE_MEDIA)) {
				mediaService.saveMedia(beforeEditMedia);
				beforeEditMedia = (mediaService.loadMediaByLanguageTypeYearTitle(beforeEditMedia));
				restoreMetaInfo();
			} else if (type.equals(ActionType.EDIT_MEDIA)) {
				mediaService.saveMedia(beforeEditMedia);
			} else {
				logger.error("Error while undoing media action, wrong action type");
			}
		}

		@Override
		void performRedoAction() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
			if (type.equals(ActionType.SAVE_MEDIA)) {
				restoreMetaInfo();
				beforeEditMedia = mediaService.saveMedia(beforeEditMedia);
			} else if (type.equals(ActionType.DELETE_MEDIA)) {
				bookmarkService.getBookmarkByMedia(beforeEditMedia).forEach(bookmarkService::deleteBookmark);
				mediaService.deleteMedia(beforeEditMedia);
			} else if (type.equals(ActionType.EDIT_MEDIA)) {
				mediaService.saveMedia(afterEditMedia);
			} else {
				logger.error("Error while redoing media action, wrong action type");
			}

		}

		private void saveMetaInfo() {
			bookMarkList = bookmarkService.getBookmarkByMedia(beforeEditMedia);
			reserveList = borrowService.getAllReservedByMedia(beforeEditMedia);
		}

		private void restoreMetaInfo() {
			for (Bookmark current : bookMarkList) {
				bookmarkService.addBookmark(current.getUser(), beforeEditMedia);
			}
			for (Reserved current : reserveList) {
				borrowService.reserveMedia(current.getUser(), beforeEditMedia);
			}
		}

	}

	private class BorrowTimeAction extends ActionItem {

		protected Collection<MediaBorrowTime> borrowTimes;

		protected BorrowTimeAction(final Collection<MediaBorrowTime> borrowTimes, final ActionType type) {
			this.type = type;
			this.borrowTimes = borrowTimes;
		}

		@Override
		void performUndoAction() {
			if (type.equals(ActionType.EDIT_MEDIA_BORROW_TIME)) {
				Collection<MediaBorrowTime> tempBorrowTimes = mediaBorrowTimeRepository.findAll();
				for (MediaBorrowTime current : borrowTimes) {
					mediaBorrowTimeRepository.save(current);
				}
				borrowTimes = tempBorrowTimes;
			}
		}

		@Override
		void performRedoAction() {
			for (MediaBorrowTime current : borrowTimes) {
				if (type.equals(ActionType.EDIT_MEDIA_BORROW_TIME)) {
					mediaBorrowTimeRepository.save(current);
				}
			}
		}

	}

	/**
	 * Enum that represents the supported actions to undo/redo.
	 */
	public enum ActionType {
		UNBORROW, BORROW, SAVE_USER, DELETE_USER, EDIT_USER, SAVE_MEDIA, DELETE_MEDIA, EDIT_MEDIA, SAVE_BOOKMARK,
		DELETE_BOOKMARK, EDIT_MEDIA_BORROW_TIME
	}

}
