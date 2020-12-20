package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.services.BookmarkService;
import java.io.Serializable;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Controller for the bookmark list view.
 *
 */
@Component
@Scope("view")

public class BookmarkListController implements Serializable {

    @Autowired
    private BookmarkService bookmarkService;

    /**
     * Returns a list of the customers bookmarks.
     *
     * @return all bookmarks
     */
    public Collection<Bookmark> getBookmarks() {
        return bookmarkService.getAllBookmarks();
    }
}



