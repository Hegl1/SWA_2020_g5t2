package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.UserService;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    /**
     * Attribute to cache the currently displayed user
     */
    private User user;
    
    private List<String> newRolesString;

    /**
     * Sets the currently displayed user and reloads it form db. This user is
     * targeted by any further calls of
     * {@link #doReloadUser()}, {@link #doSaveUser()} and
     * {@link #doDeleteUser()}.
     *
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
        //doReloadUser();
    }

    /**
     * Returns the currently displayed user.
     *
     * @return
     */
    public User getUser() {
        return user;
    }
    
    public void setNewRolesString(List<String> roles){
    	this.newRolesString = roles;
    }
    
    public List<String> getNewRolesString(){
    	return newRolesString;
    }

    /**
     * Creates User.
     */
    /*public void doCreateUser(String username, String password, String firstName, String lastName, Boolean enabled, UserRole roles, String email) {
        try {
            this.userService.createUser(username, password, firstName, lastName, enabled, roles, email);
        } catch (UserService.UnauthorizedActionException e) {
            System.out.println(e.getMessage());
            // TODO: Exception-Handling
        }
        doReloadUser();
    }*/

    /**
     * Action to force a reload of the currently displayed user.
     */
    public void doReloadUser() {
        user = userService.loadUser(user.getUsername());
    }

    /**
     * Action to save the currently displayed user.
     */
    public void doSaveUser() {
        user = this.userService.saveUser(user);
    }

    /**
     * Action to delete the currently displayed user.
     */
    
    public void doDeleteUser() {
        this.userService.deleteUser(user);
        user = null;
    }
    /*public void doDeleteUser() {
        try {
            this.userService.deleteUser(user);
        } catch (UserService.UnauthorizedActionException unauthorizedActionException) {
            System.out.println(unauthorizedActionException.getMessage());
            // TODO: Exception-Handling
        }
        user = null;
    }*/
    
    public void changeUserRoles(){
    	userService.changeUserRoles(user, newRolesString);
    }

}
