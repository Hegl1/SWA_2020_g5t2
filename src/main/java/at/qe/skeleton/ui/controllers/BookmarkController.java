package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.services.BookmarkService;
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
