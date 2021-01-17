package at.qe.skeleton.services;

import java.util.Collection;
import java.util.List;
import at.qe.skeleton.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.BookmarkRepository;

/**
 * Service for listing the customers own bookmarks.
 *
 */
@Component
@Scope("application")
public class BookmarkService {

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private UserService userService;

	/**
	 * Returns a collection of all the bookmarks.
	 *
	 * @return all bookmarks in the database
	 */
	public Collection<Bookmark> getAllBookmarks() {
		return bookmarkRepository.findAll();
	}

	/**
	 * Returns a collection of the current user bookmarks.
	 *
	 * @return only the bookmarks which belong to the current logged in users
	 * @param user
	 */
	public Collection<Bookmark> getBookmarksByUser(final User user) {

		return bookmarkRepository.findByUsername(user.getUsername());
	}

	/**
	 * Returns 1 bookmark according to a given ID.
	 *
	 * @param bookmarkID of the wanted bookmark
	 * @return the bookmark with exact this ID
	 */
	public Bookmark loadBookmark(final Long bookmarkID) {
		return bookmarkRepository.findFirstByBookmarkID(bookmarkID);
	}

	/**
	 * the following 4 functions return type specific information about the media
	 */
	public String getMediaTypeAsString(final Bookmark bookmark) {
		return bookmark.getMedia().getMediaType().toString();
	}

	public String getMediaTitle(final Bookmark bookmark) {
		return bookmark.getMedia().getTitle();
	}

	public boolean isBookmarkedForAuthenticatedUser(final Media media) {
		User myUser = userService.loadCurrentUser();
		Bookmark b_check = bookmarkRepository.findFirstByUserAndMedia(myUser, media);

		return b_check != null;
	}

	/**
	 * Deletes the bookmark.
	 *
	 * @param bookmark the bookmark to delete
	 */
	public void deleteBookmark(final Bookmark bookmark) {
		if (bookmark == null) {
			return;
		}

		bookmarkRepository.delete(bookmark);
	}

	/**
	 * add a bookmark.
	 *
	 * @param media the media to add as one's own bookmark
	 */
	public void addBookmark(final Media media) {
		User myUser = userService.loadCurrentUser();

		if (!this.isBookmarkedForAuthenticatedUser(media)) {
			Bookmark mark = new Bookmark();
			mark.setMedia(media);
			mark.setUser(myUser);
			bookmarkRepository.save(mark);
		}
	}

	/**
	 * Toggles the bookmarking state of the given media for the authenticated user
	 *
	 * @param media the media to add or to remove as/from one's own bookmarks
	 * @return true, if the media is now bookmarked, false otherwise
	 */
	public boolean toggleBookmark(final Media media) {

		if (this.isBookmarkedForAuthenticatedUser(media)) {
			this.deleteBookmark(this.getBookmarkForAuthenticatedUserByMedia(media));

			return false;
		} else {
			this.addBookmark(media);

			return true;
		}
	}

	/**
	 * Returns the bookmark for the given media for the authenticated user
	 * 
	 * @param media the media to search for
	 * @return the found bookmark
	 */
	public Bookmark getBookmarkForAuthenticatedUserByMedia(final Media media) {
		User myUser = userService.loadCurrentUser();

		return bookmarkRepository.findFirstByUserAndMedia(myUser, media);
	}

	/**
	 * Returns the bookmarks that fit to a given media
	 *
	 * @param media the media to search for
	 * @return a list of bookmarks of that media
	 */
	public List<Bookmark> getBookmarkByMedia(final Media media) {
		return bookmarkRepository.findByMedia(media);
	}

	/**
	 * add a bookmark to the repository
	 *
	 * @param bookmark
	 */
	public void saveBookmark(final Bookmark bookmark) {
		bookmarkRepository.save(bookmark);
	}

	/**
	 * add a bookmark for a specified user to the repository
	 *
	 * @param user
	 * @param media
	 */
	public void addBookmark(final User user, final Media media) {
		Bookmark bookmark = new Bookmark();
		bookmark.setMedia(media);
		bookmark.setUser(user);
		bookmarkRepository.save(bookmark);
	}
	/**
	 * return a bookmark that fits to a specific user and media
	 *
	 * @param user
	 * @param media
	 * @return bookmark
	 */
	public Bookmark getBookmarkByUserAndMedia(final User user, final Media media) {
		return this.bookmarkRepository.findFirstByUserAndMedia(user, media);
	}

}
