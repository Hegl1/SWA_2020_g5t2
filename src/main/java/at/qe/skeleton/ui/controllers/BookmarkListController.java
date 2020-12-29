package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.BookmarkService;
import java.io.Serializable;
import java.util.Collection;

import at.qe.skeleton.services.UserService;

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

    private Bookmark bookmark;

    @Autowired
    private UserService userService;

    /**
     * Returns a list of the current customers bookmarks.
     *
     * @return all bookmarks of the logged in user
     */
    public Collection<Bookmark> getBookmarks() {
        User myCurrentUser = userService.loadCurrentUser();
//        System.out.println("In controller - fetching bookmarks with: "+ myCurrentUser);
        return bookmarkService.getMyBookmarks(myCurrentUser);
    }


    /**
     * Action to delete the currently displayed bookmark.
     */
    public void doDeleteBookmark()  {

        this.bookmarkService.deleteBookmark(bookmark);
        bookmark = null;
    }



}




