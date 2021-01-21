package at.qe.skeleton.ui.controllers;

import java.io.Serializable;
import java.util.*;

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

    /**
     * Sets the currently displayed user and reloads it form db. This user is
     * targeted by any further calls of {@link #doSaveUser()} and {@link #doDeleteUser()}.
     *
     * @param user the user to set
     */
    public void setUser(final User user) {
        this.user = user;
        this.userService.refreshUser(user);
    }

    /**
     * Returns the currently displayed user.
     *
     * @return the set user
     */
    public User getUser() {
        return this.user;
    }

    public String getUserRole(){
        if(this.user == null) return null;

        Iterator<UserRole> rolesIterator = this.user.getRoles().iterator();

        if(!rolesIterator.hasNext()) return null;

        return rolesIterator.next().toString();
    }

    public void setUserRole(final String userRole){
        if(userRole == null || this.user == null) return;

        switch(userRole.toUpperCase()){
            case "ADMIN":
                setUserRole(UserRole.ADMIN);
                break;
            case "LIBRARIAN":
                setUserRole(UserRole.LIBRARIAN);
                break;
            case "CUSTOMER":
                setUserRole(UserRole.CUSTOMER);
                break;
        }
    }

    /**
     * Sets the users role
     * @param userRole
     */
    private void setUserRole(final UserRole userRole){
        if(userRole == null || this.user == null) return;

        this.user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
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

    public void reset(){
        this.user = null;
    }
}
