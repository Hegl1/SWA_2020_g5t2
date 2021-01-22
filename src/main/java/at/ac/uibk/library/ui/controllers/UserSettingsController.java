package at.ac.uibk.library.ui.controllers;

import at.ac.uibk.library.model.User;
import at.ac.uibk.library.services.UndoRedoService;
import at.ac.uibk.library.services.UserService;
import at.ac.uibk.library.utils.UnallowedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope("view")

public class UserSettingsController implements Serializable {
	@Autowired
	private UndoRedoService undoRedoService;

	@Autowired
	private UserService userService;

	@Autowired
	private FMSpamController fms;

	private User user = null;

	public void setUser(final User user) {
		if (user == null) {
			this.user = userService.loadCurrentUser();
		} else {
			this.user = user;
		}
	}

	public User getUser() {
		if (user == null) {
			user = userService.loadCurrentUser();
		}

		return user;
	}

	public void save() {
		this.undoRedoService.addAction(undoRedoService.createAction(userService.loadUser(this.user.getUsername()), user,
				UndoRedoService.ActionType.EDIT_USER));

		try {
			this.userService.saveUser(this.user);
			fms.info("Changes saved");
		} catch (UnallowedInputException e) {
			fms.warn(e.getMessage());
		}

		this.user = null;
	}

	public void reset() {
		this.user = null;
	}
}
