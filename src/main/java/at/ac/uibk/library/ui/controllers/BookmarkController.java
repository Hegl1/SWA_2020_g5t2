package at.ac.uibk.library.ui.controllers;

import at.ac.uibk.library.model.Media;
import at.ac.uibk.library.services.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope("session")

public class BookmarkController implements Serializable {
	@Autowired
	private BookmarkService bookmarkService;

	@Autowired
	FMSpamController fms;

	public void toggleBookmark(final Media media) {
		bookmarkService.toggleBookmark(media);
	}

	public boolean isBookmarkedForAuthenticatedUser(final Media media) {
		return bookmarkService.isBookmarkedForAuthenticatedUser(media);
	}
}
