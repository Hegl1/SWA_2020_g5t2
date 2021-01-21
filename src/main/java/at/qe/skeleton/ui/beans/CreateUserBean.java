package at.qe.skeleton.ui.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.MailService;
import at.qe.skeleton.services.UndoRedoService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.controllers.FMSpamController;
import net.bytebuddy.utility.RandomString;

@Component
@Scope("view")
public class CreateUserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private UserService userService;

	@Autowired
	private UndoRedoService undoRedoService;

	@Autowired
	private FMSpamController fms;

	@Autowired
	private MailService mailService;

	private User user;

	private String selectedUserRoles;

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void saveUser() {

		RandomString passwordGen = new RandomString(8);

		String password = passwordGen.nextString();
		user.setPassword(password);
		user.setEnabled(true);

		setUserRoles();

		if (userService.loadUser(user.getUsername()) != null) {
			fms.warn("Username already in use");
			return;
		}

		try {
			userService.saveUser(user);
			mailService.sendMail(user.getEmail(), "Your account was created",
					"Dear User,\nyour account has been created successfully."
							+ "\nYou can login with the following data:\nusername: " + user.getUsername() + "\n"
							+ "password: " + password + "\n Yours sincerely \n Your Library Team");
			undoRedoService.addAction(undoRedoService.createAction(user, UndoRedoService.ActionType.SAVE_USER));
			fms.info("A new user was created.");

		} catch (UserService.UnallowedInputException e) {
			fms.warn(e.getMessage());
		}

		this.user = null;
	}

	private void setUserRoles() {
		Set<UserRole> userRole = new HashSet<>();

		if (selectedUserRoles != null) {
			switch (selectedUserRoles) {
			case "librarian":
				userRole.add(UserRole.LIBRARIAN);
				break;
			case "admin":
				userRole.add(UserRole.ADMIN);
				break;
			case "customer":
				userRole.add(UserRole.CUSTOMER);
				break;
			default:
				return;
			}
		} else {
			fms.warn("No user role selected - using customer as default.");
			userRole.add(UserRole.CUSTOMER);
		}

		user.setRoles(userRole);
	}

	public User getUser() {
		if (this.user == null) {
			this.user = new User();
		}

		return this.user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public String getSelectedUserRoles() {
		return selectedUserRoles;
	}

	public void setSelectedUserRoles(final String selectedUserRoles) {
		this.selectedUserRoles = selectedUserRoles;
	}

	public void reset() {
		this.user = null;
	}
}