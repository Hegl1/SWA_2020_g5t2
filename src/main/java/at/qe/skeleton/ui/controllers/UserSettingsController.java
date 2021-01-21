package at.qe.skeleton.ui.controllers;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.User;
import at.qe.skeleton.services.UndoRedoService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.utils.UnallowedInputException;

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
