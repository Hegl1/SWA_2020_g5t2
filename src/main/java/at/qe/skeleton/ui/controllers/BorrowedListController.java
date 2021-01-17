package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.repositories.MediaBorrowTimeRepository;
import at.qe.skeleton.repositories.MediaRepository;
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

public class BorrowedListController implements Serializable {

	@Autowired
	private BorrowService borrowService;

	@Autowired
	private MediaRepository mediaRepository;

	private Borrowed borrowed;

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
	public Collection<Borrowed> getBorroweds() {
		return borrowService.getAllBorrowsByAuthenticatedUser();
	}

	public Date getBorrowedDueDate(final Borrowed borrowed) {
		Calendar c = Calendar.getInstance();
		c.setTime(borrowed.getBorrowDate());

		c.add(Calendar.DATE, mediaBorrowTimeRepository.findFirstByMediaType(borrowed.getMedia().getMediaType())
				.getAllowedBorrowTime());

		return c.getTime();
	}

	public void doUnBorrowMediaForAuthenticatedUser(final Media mediaToUnBorrow) {
		Borrowed borrow = borrowService.loadBorrowedForAuthenticatedUser(mediaToUnBorrow);
		borrowService.unBorrowMediaForAuthenticatedUser(mediaToUnBorrow);
		undoRedoService.addAction(undoRedoService.createAction(borrow, UndoRedoService.ActionType.UNBORROW));

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "The media was returned.", ""));
	}

	public void doBorrowMediaForAuthenticatedUser(final Media media) {
		borrowService.borrowMediaForAuthenticatedUser(media);
		Borrowed borrow = borrowService.loadBorrowedForAuthenticatedUser(media);
		undoRedoService.addAction(undoRedoService.createAction(borrow, UndoRedoService.ActionType.BORROW));

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "The media was borrowed.", ""));
	}
}
