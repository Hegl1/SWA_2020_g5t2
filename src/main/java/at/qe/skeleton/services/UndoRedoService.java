package at.qe.skeleton.services;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.UserService.UnauthorizedActionException;

@Component
@Scope("session")
public class UndoRedoService {

	private Deque<ActionItem> unDoQueue;

	private Deque<ActionItem> reDoQueue;

	@Autowired
	UserService userService;

	@Autowired
	MediaService mediaService;

	@Autowired
	BorrowService borrowService;

	@Autowired
	BookmarkService bookmarkService;

	private Logger logger = LoggerFactory.getLogger(UndoRedoService.class);

	private static final int MAX_SAVED_STATES = 20;

	@Autowired(required = true)
	public UndoRedoService() {
		// TODO limit size of Deque
		unDoQueue = new ArrayDeque<ActionItem>(MAX_SAVED_STATES + 1);
		reDoQueue = new ArrayDeque<ActionItem>(MAX_SAVED_STATES + 1);
	}

	public void addAction(final ActionItem action) {
		unDoQueue.push(action);
		if (unDoQueue.size() >= MAX_SAVED_STATES) {
			unDoQueue.removeLast();
		}
	}

	public void undoLastAction() {
		ActionItem action = unDoQueue.pop();
		if (action != null) {
			action.performUndoAction();
			reDoQueue.push(action);
			if (reDoQueue.size() >= MAX_SAVED_STATES) {
				reDoQueue.removeLast();
			}
		}
	}

	public void redoLastAction() {
		ActionItem action = reDoQueue.pop();
		if (action != null) {
			action.performRedoAction();
		}
	}

	public ActionItem createAction(final Borrowed borrow, final ActionType tpye) {
		return new BorrowAction(borrow, tpye);
	}

	public ActionItem createAction(final Bookmark bookmark, final ActionType type) {
		return new BookmarkAction(bookmark, type);
	}

	public ActionItem createAction(final User user, final ActionType type) {
		return new UserAction(user, type);
	}

	public ActionItem createAction(final User beforeEditUser, final User afterEditUser, final ActionType type) {
		return new UserAction(beforeEditUser, afterEditUser, type);
	}

	public ActionItem createAction(final Media media, final ActionType type) {
		return new MediaAction(media, type);
	}

	public ActionItem createAction(final Media beforeEditMedia, final Media afterEditMedia, final ActionType type) {
		return new MediaAction(beforeEditMedia, afterEditMedia, type);
	}

	public abstract class ActionItem {

		protected ActionType type;

		abstract void performUndoAction();

		abstract void performRedoAction();
	}

	private class BorrowAction extends ActionItem {

		protected Borrowed borrowed;

		protected BorrowAction(final Borrowed borrowed, final ActionType type) {
			this.borrowed = borrowed;
			this.type = type;
		}

		@Override
		protected void performUndoAction() {
			if (type.equals(ActionType.BORROW)) {
				if (!userService.loadCurrentUser().getRoles().contains(UserRole.CUSTOMER)) {
					borrowService.unBorrowMedia(borrowed);
				} else {
					borrowService.unBorrowMediaForAuthenticatedUser(borrowed.getMedia());
				}
			} else if (type.equals(ActionType.UNBORROW)) {
				borrowService.borrowMedia(borrowed.getUser(), borrowed.getMedia());
			} else {
				logger.error("Error while undoing borrow action, wrong action type");
			}
		}

		@Override
		protected void performRedoAction() {
			if (type.equals(ActionType.BORROW)) {
				borrowService.borrowMedia(borrowed.getUser(), borrowed.getMedia());
			} else if (type.equals(ActionType.UNBORROW)) {
				if (!userService.loadCurrentUser().getRoles().contains(UserRole.CUSTOMER)) {
					borrowService.unBorrowMedia(borrowed);
				} else {
					borrowService.unBorrowMediaForAuthenticatedUser(borrowed.getMedia());
				}
			} else {
				logger.error("Error while redoing borrow action, wrong action type");
			}
		}
	}

	private class BookmarkAction extends ActionItem {
		// TODO implement action in controller
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
				bookmarkService.deleteBookmark(bookmark);
			} else {
				logger.error("Error while redoing bookmark action, wrong action type");
			}
		}

	}

	private class UserAction extends ActionItem {
		// TODO implement action in controller
		protected User beforeEditUser;
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
		void performUndoAction() {
			if (type.equals(ActionType.SAVE_USER)) {
				try {
					userService.deleteUser(beforeEditUser);
				} catch (UnauthorizedActionException e) {
					logger.error("Errror while undoing user actionm unauthrized deletion of user");
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
		void performRedoAction() {
			if (type.equals(ActionType.SAVE_USER)) {
				userService.saveUser(beforeEditUser);
			} else if (type.equals(ActionType.DELETE_USER)) {
				try {
					userService.deleteUser(beforeEditUser);
				} catch (UnauthorizedActionException e) {
					logger.error("Errror while undoing user actionm unauthrized deletion of user");
				}
			} else if (type.equals(ActionType.EDIT_USER)) {
				userService.saveUser(afterEditUser);
			} else {
				logger.error("Error while redoing user action, wrong action type");
			}

		}

	}

	private class MediaAction extends ActionItem {

		protected Media beforeEditMedia;

		protected Media afterEditMedia;

		protected MediaAction(final Media media, final ActionType type) {
			this.beforeEditMedia = media;
			this.type = type;
			this.afterEditMedia = null;
		}

		protected MediaAction(final Media beforeEditMedia, final Media afterEditMedia, final ActionType type) {
			this(beforeEditMedia, type);
			this.afterEditMedia = afterEditMedia;
		}

		@Override
		void performUndoAction() {
			if (type.equals(ActionType.SAVE_MEDIA)) {
				mediaService.deleteMedia(beforeEditMedia);
			} else if (type.equals(ActionType.DELETE_MEDIA)) {
				mediaService.saveMedia(beforeEditMedia);
			} else if (type.equals(ActionType.EDIT_MEDIA)) {
				mediaService.saveMedia(beforeEditMedia);
			} else {
				logger.error("Error while undoing media action, wrong action type");
			}
		}

		@Override
		void performRedoAction() {
			if (type.equals(ActionType.SAVE_MEDIA)) {
				mediaService.saveMedia(beforeEditMedia);
			} else if (type.equals(ActionType.DELETE_MEDIA)) {
				mediaService.deleteMedia(beforeEditMedia);
			} else if (type.equals(ActionType.EDIT_MEDIA)) {
				mediaService.saveMedia(afterEditMedia);
			} else {
				logger.error("Error while redoing media action, wrong action type");
			}

		}

	}

	public enum ActionType {
		UNBORROW, BORROW, SAVE_USER, DELETE_USER, EDIT_USER, SAVE_MEDIA, DELETE_MEDIA, EDIT_MEDIA, SAVE_BOOKMARK,
		DELETE_BOOKMARK
	}

}
