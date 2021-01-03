package at.qe.skeleton.services;

import java.util.Collection;

import at.qe.skeleton.model.Book;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.BookmarkRepository;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;



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

	public String getMediaInfo(final Bookmark bookmark) {

        switch (bookmark.getMedia().getMediaType().toString()) {
            case "BOOK":
                return "Book - author";

            case "AUDIOBOOK":
                return "Audiobook - author";

            case "MAGAZINE":
                return "Magazine - series";

            case "VIDEO":
                return "Video - length";

            default:
                return "Media type not defined";
        }
    }

    public String getIfCurrentBorrowed(final Bookmark bookmark) {

        switch (bookmark.getMedia().getCurBorrowed()) {
            case 0:
                return "available";

            case 1:
                return "not available";

            default:
                return "availability status not defined";
        }
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
		if(bookmark == null) return;

            bookmarkRepository.delete(bookmark);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Bookmark was removed.",  "") );
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
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Bookmark was added.", "" ));
		}else {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage asGrowl = new FacesMessage(FacesMessage.SEVERITY_WARN, "Bookmark was already made!",  "" );
            FacesContext.getCurrentInstance().addMessage("asGrowl", asGrowl);
        }
	}

	public void toggleBookmark(final Media media) {
		User myUser = userService.loadCurrentUser();

		if(this.isBookmarkedForAuthenticatedUser(media)){
			this.deleteBookmark(bookmarkRepository.findFirstByUserAndMedia(myUser, media));
		}else{
			this.addBookmark(media);
		}
	}
}
