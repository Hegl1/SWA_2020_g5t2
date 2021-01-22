package at.ac.uibk.library.ui.controllers;

import at.ac.uibk.library.services.BookmarkService;
import at.ac.uibk.library.model.Bookmark;
import at.ac.uibk.library.model.User;
import at.ac.uibk.library.services.UserService;
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
@Scope("session")

public class BookmarkListController implements Serializable {

	@Autowired
	private BookmarkService bookmarkService;

	private Bookmark bookmark;

	@Autowired
	private UserService userService;

	@Autowired
	FMSpamController fms;

	/**
	 * Returns a list of the current customers bookmarks.
	 *
	 * @return all bookmarks of the logged in user
	 */
	public Collection<Bookmark> getBookmarks() {
		User currentUser = userService.loadCurrentUser();

		return bookmarkService.getBookmarksByUser(currentUser);
	}
}
