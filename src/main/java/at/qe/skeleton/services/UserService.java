package at.qe.skeleton.services;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
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
	// TODO: Move PasswordEncoder
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public User saveUser(final User user) {
		if (user.isNew()) {
			user.setCreateDate(new Date());
			user.setCreateUser(getAuthenticatedUser());
		} else {
			user.setUpdateDate(new Date());
			user.setUpdateUser(getAuthenticatedUser());
			// if password was changed, then encrypt it again
			Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
			if (BCRYPT_PATTERN.matcher(user.getPassword()).matches()) {
				// stringToCheck is an encoded bcrypt password.
			} else {
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				String newencodedPassword = passwordEncoder.encode(user.getPassword());
				user.setPassword(newencodedPassword);
			}

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
	// TODO @THOMAS: needs testing
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public User createUser(final String username, final String password, final String firstName,
						   final String lastName, final Boolean enabled, final UserRole roles,
						   final String email) throws UnauthorizedActionException, UnallowedInputException {

		if(this.getAuthenticatedUser().getRoles().contains(UserRole.LIBRARIAN) &&
				(roles.equals(UserRole.LIBRARIAN) || roles.equals(UserRole.ADMIN))) {
			throw new UnauthorizedActionException("Librarians may not create Admins!");
		}

		// source of following monstrosity: https://stackoverflow.com/questions/201323/how-to-validate-an-email-address-using-a-regular-expression
		String regex = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
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

		// TODO: potential issue, that an Admin has less rights if he has the Librarian-Role as wel
		//  this problem should not occur, if only one Role is allowed in the Constructor and Setter of the User

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


	/**
	 * Custom Exceptions
	 */

	public static class UnauthorizedActionException extends Exception {
		private static final long serialVersionUID = 1L;

		public UnauthorizedActionException(final String message){
			super(message);
		}
	}

	public static class UnallowedInputException extends Exception {
		private static final long serialVersionUID = 1L;

		public UnallowedInputException(final String message) {
			super(message);
		}
	}

}
