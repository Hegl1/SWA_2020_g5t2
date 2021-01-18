package at.qe.skeleton.ui.controllers;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.BorrowService;
import at.qe.skeleton.services.UndoRedoService;
import at.qe.skeleton.services.UserService;

@Component
@Scope("view")

public class BorrowForUserController implements Serializable {
	private Media media;
	private String username;

	@Autowired
	private UndoRedoService undoRedoService;

	@Autowired
	private UserService userService;

	@Autowired
	private BorrowService borrowService;

	@Autowired
	private FMSpamController fms;

	/**
	 * Returns a list of customers (users) to select one from
	 *
	 * @return the user list
	 */
	public List<User> getUserList() {
		return userService.loadCustomers();
	}

	/**
	 * Borrows the selected media for the selected username
	 *
	 * INFO: The username is used, since primefaces seamed to have a problem with
	 * setting an object
	 */
	public void borrowForUser() {
		if (username == null) {
			return;
		}

		User u = userService.loadUser(username);

		if (borrowService.borrowMedia(u, media)) {
			fms.info("The media was borrowed for user '" + username + "'");

			Borrowed borrow = borrowService.loadBorrowed(u, media);

			undoRedoService.addAction(undoRedoService.createAction(borrow, UndoRedoService.ActionType.BORROW));
		} else {
			fms.warn("The media could not be borrowed!");
		}
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(final Media media) {
		this.media = media;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}
}
