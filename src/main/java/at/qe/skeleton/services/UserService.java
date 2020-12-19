package at.qe.skeleton.services;

import java.sql.SQLOutput;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.qe.skeleton.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.UserRepository;

/**
 * Service for accessing and manipulating user data.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Scope("application")
public class UserService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Returns a collection of all users.
	 *
	 * @return
	 */
	// @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<User> getAllUsers() {
		return userRepository.findAll();
	}

	/**
	 * Loads a single user identified by its username.
	 *
	 * @param username the username to search for
	 * @return the user with the given username
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN') or principal.username eq #username")
	public User loadUser(final String username) {
		return userRepository.findFirstByUsername(username);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	public List<User> loadUserByName(String fullName) {
		return userRepository.findByWholeNameConcat(fullName);
	}

	public User loadCurrentUser() {
		return loadUser(getAuthenticatedUser().getUsername());
	}

	/**
	 * Saves the user. This method will also set {@link User#createDate} for new
	 * entities or {@link User#updateDate} for updated entities. The user requesting
	 * this operation will also be stored as {@link User#createDate} or
	 * {@link User#updateUser} respectively.
	 *
	 * @param user the user to save
	 * @return the updated user
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public User saveUser(User user) {
		if (user.isNew()) {
			user.setCreateDate(new Date());
			user.setCreateUser(getAuthenticatedUser());
		} else {
			user.setUpdateDate(new Date());
			user.setUpdateUser(getAuthenticatedUser());
		}
		return userRepository.save(user);
	}

	/**
	 * Changes the set of roles a user possesses.
	 * 
	 * @param user     The user whose roles should be changed
	 * @param newRoles Set of new roles
	 * @return if the change was succesful.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	public boolean changeUserRoles(User user, Set<UserRole> newRoles) {
		try {
			user.setRoles(newRoles);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean changeUserRoles(User user, List<String> newRolesString) {
		Set<UserRole> newRolesSet = new HashSet<>();

		for (String selected : newRolesString) {
			switch (selected.toLowerCase()) {
			case "librarian":
				newRolesSet.add(UserRole.LIBRARIAN);
				break;
			case "admin":
				newRolesSet.add(UserRole.ADMIN);
				break;
			default:
				System.err.println(
						"[Warning] UserService - changeUserRoles: Role \"" + selected + "\" not supported yet!");
				return false;
			}
		}
		user.setRoles(newRolesSet);

		return true;
	}

	/**
	 * Creates a user.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public User createUser(String username, String password, String firstName, String lastName, Boolean enabled,
			UserRole roles, String email) throws UnauthorizedActionException {

		if (this.getAuthenticatedUser().getRoles().contains(UserRole.LIBRARIAN)
				&& (roles.equals(UserRole.LIBRARIAN) || roles.equals(UserRole.ADMIN))) {
			throw new UnauthorizedActionException("Librarians may not create Admins!");
		}

		User createdUser = new User(username, password, firstName, lastName, enabled, roles, email);

		this.userRepository.save(createdUser);
		return createdUser;
	}

	/**
	 * Deletes the user.
	 *
	 * @param user the user to delete
	 */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(User user) {
        userRepository.delete(user);
        // :TODO: write some audit log stating who and when this user was permanently
        // deleted.
    }
/*	
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void deleteUser(final User user) throws UnauthorizedActionException {

		// TODO: potential issue, that an Admin has less rights if he has the
		// Librarian-Role as well
		// this problem should not occur, if only one Role is allowed in the Constructor
		// and Setter of the User

		if (this.getAuthenticatedUser().getRoles().contains(UserRole.LIBRARIAN)
				&& user.getRoles().contains(UserRole.ADMIN)) {

			throw new UnauthorizedActionException("Librarian may not delete Administrators!");

		} else if (this.getAuthenticatedUser().getId().equals(user.getId())) {

			throw new UnauthorizedActionException("Users may not delete themself!");

		} else {
			userRepository.delete(user);
		}
	}*/

	private User getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findFirstByUsername(auth.getName());
	}

	public static class UnauthorizedActionException extends Exception {
		private static final long serialVersionUID = 1L;

		public UnauthorizedActionException(String message) {
			super(message);
		}
	}

}
