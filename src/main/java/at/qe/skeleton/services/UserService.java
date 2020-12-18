package at.qe.skeleton.services;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<User> getAllUsers() {
		return this.userRepository.findAll();
	}

	/**
	 * Loads a single user identified by its username.
	 *
	 * @param username the username to search for
	 * @return the user with the given username
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN') or principal.username eq #username")
	public User loadUser(final String username) {
		return this.userRepository.findFirstByUsername(username);
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
	public User saveUser(final User user) {
		return this.userRepository.save(user);
	}

	/**
	 * Creates a user.
	 */

	// TODO THOMAS: TEST THIS SHIT!
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public User createUser(final String username, final String password, final String firstName, final String lastName, final Boolean enabled, final UserRole roles, final String email) throws UnauthorizedActionException, UnallowedInputException {

		if(this.getAuthenticatedUser().getRoles().contains(UserRole.LIBRARIAN) &&
				(roles.equals(UserRole.LIBRARIAN) || roles.equals(UserRole.ADMIN))) {
			throw new UnauthorizedActionException("Librarians may not create Admins!");
		}

		String regex = "^(.+)@(.+)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);

		if(!matcher.matches()) {
			throw new UnallowedInputException("Unallowed input for Email!");
		}

		User createdUser = new User(username, password, firstName, lastName, enabled, roles, email);

		this.saveUser(createdUser);
		return createdUser;
	}

	/**
	 * Deletes the user.
	 *
	 * @param user the user to delete
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void deleteUser(final User user) throws UnauthorizedActionException {

		// TODO: potential issue, that an Admin has less rights if he has the Librarian-Role as well
		// this problem should not occur, if only one Role is allowed in the Constructor and Setter of the User

		if(this.getAuthenticatedUser().getRoles().contains(UserRole.LIBRARIAN) &&
				user.getRoles().contains(UserRole.ADMIN)) {

			throw new UnauthorizedActionException("Librarian may not delete Administrators!");

		} else if (this.getAuthenticatedUser().getId().equals(user.getId())) {

			throw new UnauthorizedActionException("Users may not delete themself!");

		} else {
			this.userRepository.delete(user);
		}
	}

	private User getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return this.userRepository.findFirstByUsername(auth.getName());
	}

	public static class UnauthorizedActionException extends Exception {
		private static final long serialVersionUID = 1L;

		public UnauthorizedActionException(String message){
			super(message);
		}
	}

	public static class UnallowedInputException extends Exception {
		private static final long serialVersionUID = 1L;

		public UnallowedInputException(String message) {
			super(message);
		}
	}
}
