package at.qe.skeleton.ui.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.BorrowService;
import at.qe.skeleton.services.UndoRedoService;
import at.qe.skeleton.services.UserService;

/**
 * Controller for the user detail view.
 * <p>
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
    private FMSpamController fms;

    @Autowired
    private BorrowService borrowService;

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
            fms.warn(e.getMessage());
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
        this.undoRedoService.addAction(undoRedoService.createAction(userService.loadUser(this.user.getUsername()), user,
                UndoRedoService.ActionType.EDIT_USER));
        try {
            this.user = this.userService.saveUser(this.user);
        } catch (UserService.UnallowedInputException e) {
            fms.warn(e.getMessage());
        }

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
                fms.info("The user was deleted and all his bookmarks and reserved media has been deleted");
                this.userService.deleteUser(this.user);
                this.undoRedoService
                        .addAction(undoRedoService.createAction(user, UndoRedoService.ActionType.DELETE_USER));

            } catch (UserService.UnauthorizedActionException unauthorizedActionException) {
                fms.warn("Cannot delete onw user.");

            }
            this.user = null;
        }

    }

    public void changeUserRoles() {
        try {
            userService.changeUserRoles(user, newRolesString);
            this.newRolesString = null;
            fms.info("Role edit done successfully.");

        } catch (UserService.UnallowedInputException
                unauthorizedActionException) {
            fms.warn("Multiple roles cannot be set.");
        }
    }

    public void setAsAdmin() {
        List<String> newRolesString2 = new ArrayList<>();
        newRolesString2.add("admin");
        try {
            userService.changeUserRoles(user, newRolesString2);
            this.newRolesString = null;
            fms.info("Admin Role set successfully.");
        } catch (UserService.UnallowedInputException
                unauthorizedActionException) {
            fms.warn("Did not work.");
        }

    }

    public void setAsLibrarian() {
        List<String> newRolesString2 = new ArrayList<>();
        newRolesString2.add("librarian");
        try {
            userService.changeUserRoles(user, newRolesString2);
            this.newRolesString = null;
            fms.info("Librarian Role set successfully.");
        } catch (UserService.UnallowedInputException
                unauthorizedActionException) {
            fms.warn("Did not work.");
        }
    }

    public void setAsCustomer() {
        List<String> newRolesString2 = new ArrayList<>();
        newRolesString2.add("customer");
        try {
            userService.changeUserRoles(user, newRolesString2);
            this.newRolesString = null;
            fms.info("Customer Role set successfully.");
        } catch (UserService.UnallowedInputException
                unauthorizedActionException) {
            fms.warn("Did not work.");
        }
    }


    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }
}
