package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.Reserved;
import at.qe.skeleton.services.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * Controller for the borrowed list view.
 *
 */
@Component
@Scope("view")

public class ReservedController implements Serializable {

    @Autowired
    private BorrowService borrowService;

    /**
     * Returns the amount of reservations made for a certain media
     *
     * @param media the media to search for
     * @return the count of reservations made
     */
    public int getReservationCountForMedia(final Media media){
        return borrowService.getReservationCountForMedia(media);
    }

    /**
     * Method that reserves a Media for the currently authenticaed User.
     *
     * @param media the Media which should be reserved.
     */
    public void doReserveMediaForAuthenticatedUser(final Media media){
        borrowService.reserveMediaForAuthenticatedUser(media);

        // TODO: add growl message
    }

    /**
     * Removes a reservation of the media for the authenticated user
     *
     * @param media the media
     */
    public void doRemoveReservationForAuthenticatedUser(final Media media){
        borrowService.removeReservationForAuthenticatedUser(media);

        // TODO: add growl message
    }

    /**
     * Toggles the reservation status of the media for the authenticated user
     *
     * @param media the media
     */
    public void toggleReservationForAuthenticatedUser(final Media media){
        if(isReservedForAuthenticatedUser(media)){
            this.doRemoveReservationForAuthenticatedUser(media);
        }else{
            this.doReserveMediaForAuthenticatedUser(media);
        }
    }

    /**
     * Returns whether the authenticated user has reserved this media or not
     *
     * @param media the media to search for
     * @return true, if he has reserved it, false otherwise
     */
    public boolean isReservedForAuthenticatedUser(final Media media) {
        return borrowService.isReservedForAuthenticatedUser(media);
    }

    public Collection<Reserved> getReservedList() {
        return borrowService.getAllReservedByAuthenticatedUser();
    }
}
