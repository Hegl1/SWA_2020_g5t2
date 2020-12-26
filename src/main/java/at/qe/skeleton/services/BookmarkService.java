package at.qe.skeleton.services;

import java.util.Collection;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.repositories.BookmarkRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Bookmark;

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
    private MediaService mediaService_2;

    /**
     * Returns a collection of all the customers bookmarks.
     *
     * @return all bookmarks the current logged in users has
     */
    public Collection<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }

    /**
     * Returns 1 bookmark according to a given ID.
     *
     * @param bookmarkID of the wanted bookmark
     * @return the bookmark with exact this ID
     */
    public Bookmark loadBookmark(final Long bookmarkID ) { return bookmarkRepository.findFirstByBookmarkID(bookmarkID);}

    /**
     * the following 4 functions return type specific information about the media
     * */
    public String getThatType(final Bookmark bookmark) {   return bookmark.getMedia().getMediaType().toString(); }

    public String getThatTitle(final Bookmark bookmark) {   return bookmark.getMedia().getTitle(); }

    public String getThatAddInfo(final Bookmark bookmark) {

        switch (bookmark.getMedia().getMediaType().toString()) {
            case "BOOK":
                return "Buch - Author";

            case "AUDIOBOOK":
                return "AudioBuch - Author";

            case "MAGAZINE":
                return "Magazin - Serie";

            case "VIDEO":
                return "Video - Länge";

            default:
                return "nicht definierter Medientyp";
        }
    }

    public String getIfCurrentBorrowed(final Bookmark bookmark) {

        switch (bookmark.getMedia().getCurBorrowed()) {
            case 0:
                return "0 - frei";

            case 1:
                return "1 - ausgeliehen";

            default:
                return "Status nicht verfügbar";
        }
    }



    /**
     * Deletes the bookmark.
     *
     * @param bookmark the bookmark to delete
     */
    public void deleteBookmark(Bookmark bookmark) {

            bookmarkRepository.delete(bookmark);
        }


}
