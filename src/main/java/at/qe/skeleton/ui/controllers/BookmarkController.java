package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.services.BookmarkService;
import at.qe.skeleton.services.UndoRedoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope("view")

public class BookmarkController implements Serializable {
    @Autowired
    private UndoRedoService undoRedoService;

    @Autowired
    private BookmarkService bookmarkService;

    public void toggleBookmark(Media media){
        UndoRedoService.ActionType action;

        if(bookmarkService.toggleBookmark(media)){
            action = UndoRedoService.ActionType.SAVE_BOOKMARK;
        }else{
            action = UndoRedoService.ActionType.DELETE_BOOKMARK;
        }

        undoRedoService.addAction(undoRedoService.createAction(bookmarkService.getBookmarkForAuthenticatedUserByMedia(media), action));
    }

    public boolean isBookmarkedForAuthenticatedUser(Media media){
        return bookmarkService.isBookmarkedForAuthenticatedUser(media);
    }
}
