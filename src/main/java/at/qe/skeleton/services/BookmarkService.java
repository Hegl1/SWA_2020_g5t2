package at.qe.skeleton.services;

import java.util.Collection;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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

    @Autowired
    private UserService userService;

    /**
     * Returns a collection of all the bookmarks.
     * @return all bookmarks in the database
     */
    public Collection<Bookmark> getAllBookmarks() { return bookmarkRepository.findAll(); }

    /**
     * Returns a collection of the current user bookmarks.
     *
     * @return only the bookmarks which belong to the current logged in users
     * @param myCurrentUser
     */
    public Collection<Bookmark> getMyBookmarks(User myCurrentUser) {

        return bookmarkRepository.findMine(myCurrentUser.getUsername());}

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

    /**
     * add a bookmark.
     *
     * @param media the media to add as one's own bookmark
     */
    public void addBookmark(Media media) {

        System.out.println("add the media as bookmark to the own user");
        User myUser = userService.loadCurrentUser();
        bookmarkRepository.add(media, myUser.getUsername());
        System.out.println("done, nice!");

    }



}
