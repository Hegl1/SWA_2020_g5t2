package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.UserService;
import java.io.Serializable;
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

    /**
     * Sets the currently displayed user and reloads it form db. This user is
     * targeted by any further calls of
     * {@link #doReloadUser()}, {@link #doSaveUser()} and
     * {@link #doDeleteUser()}.
     *
     * @param user
     */
    public void setUser(final User user) {
        this.user = user;
        this.doReloadUser();
    }

    /**
     * Returns the currently displayed user.
     *
     * @return
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Creates User.
     */
    public void doCreateUser(final String username, final String password, final String firstName, final String lastName, final Boolean enabled, final UserRole roles, final String email) {
        try {
            this.userService.createUser(username, password, firstName, lastName, enabled, roles, email);
        } catch (UserService.UnauthorizedActionException | UserService.UnallowedInputException e) {
            System.out.println(e.getMessage());
            // TODO: Exception-Handling
        }
        this.doReloadUser();
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
        this.user = this.userService.saveUser(this.user);
    }

    /**
     * Action to delete the currently displayed user.
     */
    public void doDeleteUser() {
        try {
            this.userService.deleteUser(this.user);
        } catch (UserService.UnauthorizedActionException unauthorizedActionException) {
            System.out.println(unauthorizedActionException.getMessage());
            // TODO: Exception-Handling
        }
        this.user = null;
    }

}
