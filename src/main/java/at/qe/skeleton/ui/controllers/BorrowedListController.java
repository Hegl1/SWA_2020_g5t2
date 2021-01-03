package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.repositories.MediaBorrowTimeRepository;
import at.qe.skeleton.services.BorrowService;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * Controller for the borrowed list view.
 *
 */
@Component
@Scope("view")

public class BorrowedListController implements Serializable {

    @Autowired
    private BorrowService borrowService;

    private Borrowed borrowed;

    @Autowired
    private UserService userService;

    @Autowired
    private MediaBorrowTimeRepository mediaBorrowTimeRepository;

    /**
     * Returns a list of the current customers borrowed articles.
     *
     * @return all borrowed things of the logged in user
     */
    public Collection<Borrowed> getBorroweds() {
        return borrowService.getAllBorrowsByAuthenticatedUser();
    }

    public Date getBorrowedDueDate(Borrowed borrowed){
        Calendar c = Calendar.getInstance();
        c.setTime(borrowed.getBorrowDate());

        c.add(Calendar.DATE, mediaBorrowTimeRepository.findFirstByMediaType(borrowed.getMedia().getMediaType()).getAllowedBorrowTime());

        return c.getTime();
    }



}




