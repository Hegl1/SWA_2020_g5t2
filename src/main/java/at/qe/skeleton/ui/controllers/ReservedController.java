package at.qe.skeleton.ui.controllers;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.Reserved;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.BorrowService;

/**
 * Controller for the borrowed list view.
 *
 */
@Component
@Scope("view")

public class ReservedController implements Serializable {

	@Autowired
	private BorrowService borrowService;

	@Autowired
	private FMSpamController fms;

	/**
	 * Returns the amount of reservations made for a certain media
	 *
	 * @param media the media to search for
	 * @return the count of reservations made
	 */
	public int getReservationCountForMedia(final Media media) {
		return borrowService.getReservationCountForMedia(media);
	}

	/**
	 * Method that reserves a Media for the currently authenticated User.
	 *
	 * @param media the Media which should be reserved.
	 */
	public void doReserveMediaForAuthenticatedUser(final Media media) {
		borrowService.reserveMediaForAuthenticatedUser(media);

		fms.info("Media was reserved");
	}

	/**
	 * Removes a reservation of the media for the authenticated user
	 *
	 * @param media the media
	 */
	public void doRemoveReservationForAuthenticatedUser(final Media media) {
		borrowService.removeReservationForAuthenticatedUser(media);

		fms.info("Reservation was cancelled");
	}

	/**
	 * Removes a reservation of the media for a specific user
	 *
	 * @param media the media
	 * @param user  the user
	 *
	 */
	public void doRemoveReservationForSpecificUser(final Media media, final User user) {
		borrowService.removeReservationForSpecificUser(media, user);

		fms.info("Reservation was cancelled for a user");
	}

	/**
	 * Toggles the reservation status of the media for the authenticated user
	 *
	 * @param media the media
	 */
	public void toggleReservationForAuthenticatedUser(final Media media) {
		if (isReservedForAuthenticatedUser(media)) {
			this.doRemoveReservationForAuthenticatedUser(media);
		} else {
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

	/**
	 * Returns whether the specific user has reserved this media or not
	 *
	 * @param media the media to search for
	 * @param user  the user to search for
	 * @return true, if he/she has reserved it, false otherwise
	 */
	public boolean isReservedForSpecialUser(final Media media, final User user) {
		return borrowService.isReservedForSpecialUser(media, user);
	}

	public Collection<Reserved> getReservedList() {
		return borrowService.getAllReservedByAuthenticatedUser();
	}
}
