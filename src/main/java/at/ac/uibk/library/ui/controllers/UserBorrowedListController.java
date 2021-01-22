package at.ac.uibk.library.ui.controllers;

import at.ac.uibk.library.model.Borrowed;
import at.ac.uibk.library.model.User;
import at.ac.uibk.library.repositories.MediaBorrowTimeRepository;
import at.ac.uibk.library.services.BorrowService;
import at.ac.uibk.library.services.UndoRedoService;
import at.ac.uibk.library.services.UserService;
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

public class UserBorrowedListController implements Serializable {
	private User user;

	private boolean loaded = false;

	@Autowired
	private BorrowService borrowService;

	@Autowired
	private UserService userService;

	@Autowired
	private MediaBorrowTimeRepository mediaBorrowTimeRepository;

	@Autowired
	private UndoRedoService undoRedoService;

	@Autowired
	private FMSpamController fms;

	/**
	 * Returns a list of the current customers borrowed articles.
	 *
	 * @return all borrowed things of the logged in user
	 */
	public Collection<Borrowed> getBorrowings() {
		return borrowService.getAllBorrowsByUser(user);
	}

	/**
	 * Returns the date, when the borrowed media will be due
	 *
	 * @param borrowed The borrowed-object
	 * @return the calculated due date
	 */
	public Date getBorrowedDueDate(final Borrowed borrowed) {
		Calendar c = Calendar.getInstance();
		c.setTime(borrowed.getBorrowDate());

		c.add(Calendar.DATE, mediaBorrowTimeRepository.findFirstByMediaType(borrowed.getMedia().getMediaType())
				.getAllowedBorrowTime());

		return c.getTime();
	}

	/**
	 * Unborrowes the media for the set user
	 * 
	 * @param borrow the borrow-object to unborrow
	 */
	public void doUnBorrowForUser(final Borrowed borrow) {
		borrowService.unBorrowMedia(borrow);
		undoRedoService.addAction(undoRedoService.createAction(borrow, UndoRedoService.ActionType.UNBORROW));

		fms.info("The media was returned.");
	}

	/**
	 * Sets the username and fetches the user with that username If the user was not
	 * found, it will display an error message
	 * 
	 * @param username the username to set
	 */
	public void setUsername(final String username) {
		if (username == null) {
			return;
		}

		loaded = false;

		this.user = userService.loadUser(username);

		if (this.user == null) {
			fms.error("This user does not exist");
		} else {
			loaded = true;
		}
	}

	public boolean getLoaded() {
		return loaded;
	}
}
