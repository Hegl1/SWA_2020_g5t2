package at.qe.skeleton.services;

import java.util.ArrayDeque;
import java.util.Deque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.UserRole;

@Component
@Scope("session")
public class UndoRedoService {

	private Deque<ActionItem> unDoQueue;

	private Deque<ActionItem> reDoQueue;

	@Autowired
	UserService userService;

	@Autowired
	BorrowService borrowService;

	@Autowired(required = true)
	public UndoRedoService() {
		unDoQueue = new ArrayDeque<ActionItem>(20);
		reDoQueue = new ArrayDeque<ActionItem>(20);
	}

	public void addAction(final ActionItem action) {
		unDoQueue.push(action);
	}

	public void undoLastAction() {
		ActionItem action = unDoQueue.pop();
		if (action != null) {
			action.performUndoAction();
			reDoQueue.push(action);
		}
	}

	public void redoLastAction() {
		ActionItem action = reDoQueue.pop();
		if (action != null) {
			action.performRedoAction();
			addAction(action);
		}
	}

	public ActionItem createAction(final Borrowed borrow, final ActionType tpye) {
		return new BorrowAction(borrow, tpye);
	}

	public interface ActionItem {
		void performUndoAction();

		void performRedoAction();
	}

	private class BorrowAction implements ActionItem {

		Borrowed borrowed;
		ActionType type;

		public BorrowAction(final Borrowed borrowed, final ActionType type) {
			this.borrowed = borrowed;
			this.type = type;
		}

		@Override
		public void performUndoAction() {
			if (type.equals(ActionType.BORROW)) {
				if (!userService.loadCurrentUser().getRoles().contains(UserRole.CUSTOMER)) {
					borrowService.unBorrowMedia(borrowed);
				} else {
					borrowService.unBorrowMediaForAuthenticatedUser(borrowed.getMedia());
				}
			} else if (type.equals(ActionType.UNBORROW)) {
				borrowService.borrowMedia(borrowed.getUser(), borrowed.getMedia());
			}
		}

		@Override
		public void performRedoAction() {
			if (type.equals(ActionType.BORROW)) {
				borrowService.borrowMedia(borrowed.getUser(), borrowed.getMedia());
			} else if (type.equals(ActionType.UNBORROW)) {
				if (!userService.loadCurrentUser().getRoles().contains(UserRole.CUSTOMER)) {
					borrowService.unBorrowMedia(borrowed);
				} else {
					borrowService.unBorrowMediaForAuthenticatedUser(borrowed.getMedia());
				}
			}
		}
	}

	private class BookmarkAction {

	}

	private class UserAction {

	}

	private class MediaAction {

	}

	public enum ActionType {
		UNBORROW, BORROW, CREATE_USER, EDIT_USER, DELETE_USER, CREATE_MEDIA, EDIT_MEDIA, DELETE_MEDIA, ADD_BOOKMARK,
		DELETE_BOOKMARK,
	}

}
