package at.qe.skeleton.services;

import java.util.Collection;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Bookmark;
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





}
