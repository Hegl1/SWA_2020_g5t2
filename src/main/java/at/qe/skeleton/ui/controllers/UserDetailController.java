package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.BorrowService;
import at.qe.skeleton.services.UndoRedoService;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Controller for the user detail view.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Scope("view")
public class UserDetailController implements Serializable {

	@Autowired
	private UserService userService;

	@Autowired
	private UndoRedoService undoRedoService;

	@Autowired
	FMSpamController fms;

	@Autowired
	BorrowService borrowService;



	/**
	 * Attribute to cache the currently displayed user
	 */
	private User user;

	private List<String> newRolesString;

	/**
	 * Sets the currently displayed user and reloads it form db. This user is
	 * targeted by any further calls of {@link #doReloadUser()},
	 * {@link #doSaveUser()} and {@link #doDeleteUser()}.
	 *
	 * @param user the user to set
	 */
	public void setUser(final User user) {
		this.user = user;
		// this.doReloadUser();
	}

	/**
	 * Returns the currently displayed user.
	 *
	 * @return the set user
	 */
	public User getUser() {
		return this.user;
	}

	public void setNewRolesString(final List<String> roles) {
		this.newRolesString = roles;
	}

	public List<String> getNewRolesString() {
		return newRolesString;
	}

	/**
	 * Creates User.
	 */
	public void doCreateUser(final String username, final String password, final String firstName,
			final String lastName, final Boolean enabled, final UserRole roles, final String email) {

		try {
			this.userService.createUser(username, password, firstName, lastName, enabled, roles, email);
			this.undoRedoService.addAction(undoRedoService.createAction(user, UndoRedoService.ActionType.SAVE_USER));
		} catch (UserService.UnauthorizedActionException | UserService.UnallowedInputException e) {
			System.out.println(e.getMessage());
			// TODO: Exception-Handling
		}
		this.doReloadUser();
		fms.info("A new user with the username: "+ username +" was created!");
		fms.info("Please reload the page.");
	}

	/**
	 * Action to force a reload of the currently displayed user.
	 */
	public void doReloadUser() {
		this.user = this.userService.loadUser(this.user.getUsername());
	}

	/**
	 * Action to save the currently displayed user.
	 */
	public void doSaveUser() {
		this.undoRedoService.addAction(undoRedoService.createAction(userService.loadUser(this.user.getUsername()), user,
				UndoRedoService.ActionType.EDIT_USER));
		this.user = this.userService.saveUser(this.user);
		fms.info("Changes saved");
	}

	/**
	 * Action to delete the currently displayed user.
	 */
	public void doDeleteUser() {

		Collection<Borrowed> d1 = borrowService.getAllBorrowsByUser(this.user);
		if (d1.size() > 0) {
				fms.warn("This user has still borrowed: " + d1.size() + " article(s) and cannot be deleted yet.");
		} else {
				try {
					this.userService.deleteUser(this.user);
					this.undoRedoService.addAction(undoRedoService.createAction(user, UndoRedoService.ActionType.DELETE_USER));

				} catch (UserService.UnauthorizedActionException unauthorizedActionException) {
					System.out.println(unauthorizedActionException.getMessage());

				}
				this.user = null;
				fms.info("The user was deleted and all his bookmarks and reserved media has been deleted");
				fms.info("Please reload the page.");
		}

	}

	public void changeUserRoles() {
		userService.changeUserRoles(user, newRolesString);
		fms.info("Role edit done successfully.");
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
