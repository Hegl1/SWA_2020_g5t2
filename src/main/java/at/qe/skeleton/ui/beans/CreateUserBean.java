package at.qe.skeleton.ui.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.CreateUserBean;
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

    private User user;

    private List<String> selectedUserRoles;

    @PostConstruct
    public void init() {
        this.user = new User();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void saveUser() {

        RandomString passwordGen = new RandomString(8);

        String password = passwordGen.nextString();
        user.setPassword(password);
        user.setEnabled(true);

        setUserRoles();
            //save user
            userService.saveUser(user);
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
                default:
                    System.err.println("[Warning] CreateUserBean - setUserRoles: Role \"" + selected + "\" not supported yet!");
            }
        }

        user.setRoles(userRole);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getSelectedUserRoles() {
        return selectedUserRoles;
    }

    public void setSelectedUserRoles(List<String> selectedUserRoles) {
        this.selectedUserRoles = selectedUserRoles;
    }

}