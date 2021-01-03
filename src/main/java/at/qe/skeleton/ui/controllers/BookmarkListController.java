package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.BookmarkService;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * Controller for the bookmark list view.
 *
 */
@Component
@Scope("view")

public class BookmarkListController implements Serializable {

	@Autowired
	private BookmarkService bookmarkService;

	private Bookmark bookmark;

	@Autowired
	private UserService userService;

	/**
	 * Returns a list of the current customers bookmarks.
	 *
	 * @return all bookmarks of the logged in user
	 */
	public Collection<Bookmark> getBookmarks() {
		User currentUser = userService.loadCurrentUser();

		return bookmarkService.getBookmarksByUser(currentUser);
	}

	/**
	 * Action to delete the currently displayed bookmark.
	 */
	public void doDeleteBookmark() {

		this.bookmarkService.deleteBookmark(bookmark);
		bookmark = null;
	}

}
