package at.qe.skeleton.ui.controllers;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.services.BookmarkService;
import at.qe.skeleton.services.UndoRedoService;

@Component
@Scope("view")

public class BookmarkController implements Serializable {
	@Autowired
	private UndoRedoService undoRedoService;

	@Autowired
	private BookmarkService bookmarkService;

	public void toggleBookmark(final Media media) {
		UndoRedoService.ActionType action;
		Bookmark target = bookmarkService.getBookmarkForAuthenticatedUserByMedia(media);

		if (bookmarkService.toggleBookmark(media)) {
			action = UndoRedoService.ActionType.SAVE_BOOKMARK;
			target = bookmarkService.getBookmarkForAuthenticatedUserByMedia(media);
		} else {
			action = UndoRedoService.ActionType.DELETE_BOOKMARK;
		}

		undoRedoService.addAction(undoRedoService.createAction(target, action));
	}

	public boolean isBookmarkedForAuthenticatedUser(final Media media) {
		return bookmarkService.isBookmarkedForAuthenticatedUser(media);
	}
}
