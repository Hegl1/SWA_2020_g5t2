package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Controller for the user list view.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Scope("session")
public class UserListController implements Serializable {

    @Autowired
    UserService userService;

    /**
     * Returns a list of all users.
     */
    public Collection<User> getUsers() {
        return this.userService.getAllUsers();
    }

    /**
     * Converts the user role into a formatted, human-readable string (first letter
     * uppercase, the rest lower case)
     *
     * @return the converted user role
     */
    public String getUserRoleHuman(final Set<UserRole> userRoles) {
        Iterator<UserRole> rolesIterator = userRoles.iterator();

        if(!rolesIterator.hasNext()) return null;

        UserRole userRole = rolesIterator.next();
        return userRole.toString().substring(0, 1).toUpperCase() + userRole.toString().substring(1).toLowerCase();
    }
}
