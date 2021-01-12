package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.MediaBorrowTimeRepository;
import at.qe.skeleton.services.BorrowService;
import at.qe.skeleton.services.UndoRedoService;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
	 * @param borrow the borrow-object to unborrow
	 */
	public void doUnBorrowForUser(final Borrowed borrow) {
		borrowService.unBorrowMedia(borrow);
		undoRedoService.addAction(undoRedoService.createAction(borrow, UndoRedoService.ActionType.UNBORROW));

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "The media was returned.", ""));
	}

	/**
	 * Sets the username and fetches the user with that username
	 * If the user was not found, it will display an error message
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		if(username == null) return;

		loaded = false;

		this.user = userService.loadUser(username);

		if(this.user == null){
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "This user does not exist", ""));
		}else{
			loaded = true;
		}
	}

	public boolean getLoaded() {
		return loaded;
	}
}