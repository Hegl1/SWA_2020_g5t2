package at.ac.uibk.library.ui.controllers;

import at.ac.uibk.library.model.Borrowed;
import at.ac.uibk.library.model.Media;
import at.ac.uibk.library.repositories.MediaBorrowTimeRepository;
import at.ac.uibk.library.services.BorrowService;
import at.ac.uibk.library.services.UndoRedoService;
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
@Scope("session")
public class BorrowedListController implements Serializable {
	@Autowired
	private BorrowService borrowService;

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

		fms.info("The media was returned.");
	}

	public void doBorrowMediaForAuthenticatedUser(final Media media) {
		borrowService.borrowMediaForAuthenticatedUser(media);
		Borrowed borrow = borrowService.loadBorrowedForAuthenticatedUser(media);
		undoRedoService.addAction(undoRedoService.createAction(borrow, UndoRedoService.ActionType.BORROW));

		fms.info("The media was borrowed.");
	}
}
