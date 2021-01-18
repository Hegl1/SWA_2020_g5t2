package at.qe.skeleton.ui.beans;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.UndoRedoService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.controllers.FMSpamController;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	FMSpamController fms;

	private User user;

	private List<String> selectedUserRoles;



	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void saveUser(){
		RandomString passwordGen = new RandomString(8);

		String password = passwordGen.nextString();
		user.setPassword("passwd");
		user.setEnabled(true);

		setUserRoles();

		try {
			userService.saveUser(user);
		} catch (UserService.UnallowedInputException e) {
			fms.warn(e.getMessage());
		}

		undoRedoService.addAction(undoRedoService.createAction(user, UndoRedoService.ActionType.SAVE_USER));

		fms.info("Changes saved!");
		fms.info("Please reload the page.");

		this.user = null;
	}

	private void setUserRoles() {
		Set<UserRole> userRole = new HashSet<>();

		for (String selected : selectedUserRoles) {
			switch (selected) {
			case "librarian":
				userRole.add(UserRole.LIBRARIAN);
				break;
			case "admin":
				userRole.add(UserRole.ADMIN);
				break;
			case "customer":
				userRole.add(UserRole.CUSTOMER);
				break;
			default: return;
			}
		}

		user.setRoles(userRole);
	}

	public User getUser() {
		if(this.user == null){
			this.user = new User();
		}

		return this.user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public List<String> getSelectedUserRoles() {
		return selectedUserRoles;
	}

	public void setSelectedUserRoles(final List<String> selectedUserRoles) {
		this.selectedUserRoles = selectedUserRoles;
	}

	public void reset(){
		this.user = null;
	}
}