package at.ac.uibk.library.services;

import at.ac.uibk.library.model.*;
import at.ac.uibk.library.repositories.BookmarkRepository;
import at.ac.uibk.library.repositories.BorrowedRepository;
import at.ac.uibk.library.repositories.ReservedRepository;
import at.ac.uibk.library.repositories.UserRepository;
import at.ac.uibk.library.utils.UnallowedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

	@Autowired
	private BorrowedRepository borrowedRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private ReservedRepository reservedRepository;

	@Autowired
	private MailService mailService;

	/**
	 * Returns a collection of all users.
	 *
	 * @return the collection of all users
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<User> getAllUsers() {
		return this.userRepository.findAll();
	}

	/**
	 * For admins: returns a collection of all users For librarians: returns a
	 * collection of all customers
	 *
	 * @return the collection of users
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<User> getAllUsersForAuthority() {
		User currentUser = this.getAuthenticatedUser();

		if (currentUser.hasRole("LIBRARIAN")) {
			return this.userRepository.findByRole(UserRole.CUSTOMER);
		}

		return this.userRepository.findAll();
	}

	/**
	 * Loads a single user identified by its username.
	 *
	 * @param username the username to search for
	 * @return the user with the given username
	 */
	public User loadUser(final String username) {
		return this.userRepository.findFirstByUsername(username);
	}

	/**
	 * Loads a user identified by its full name
	 *
	 * @param fullName the full name of a user to search for
	 * @return the user found by its full name
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<User> loadUserByName(final String fullName) {
		return userRepository.findByWholeNameConcat(fullName);
	}

	/**
	 * Loads the currently authenticated user
	 *
	 * @return the the currently authenticated user
	 */
	public User loadCurrentUser() {
		return loadUser(getAuthenticatedUser().getUsername());
	}

	/**
	 * Loads all customers from the database
	 *
	 * @return the list of customers
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public List<User> loadCustomers() {
		return this.userRepository.findByRole(UserRole.CUSTOMER);
	}

	/**
	 * Saves the user in the user repository.
	 *
	 * @param user the user to save
	 * @return the updated user
	 */
	public User saveUser(final User user) throws UnallowedInputException {

		// source of following monstrosity:
		// https://stackoverflow.com/questions/201323/how-to-validate-an-email-address-using-a-regular-expression
		String regex = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(user.getEmail());

		if (!matcher.matches()) {
			throw new UnallowedInputException("Unallowed input for Email!");
		}

		if (user.getUsername().length() < 1 || user.getFirstName().length() < 1 || user.getLastName().length() < 1) {
			throw new UnallowedInputException("All fields need to be filled out.");
		}

		if (user.isNew()) {
			user.setCreateDate(new Date());
		} else {
			user.setUpdateDate(new Date());
			user.setPassword(user.getPassword());

		}
		return userRepository.save(user);
	}

	/**
	 * Changes the set of roles a user possesses.
	 *
	 * @param user     The user whose roles should be changed
	 * @param newRoles Set of new roles
	 * @return if the change was successful.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	public boolean changeUserRoles(final User user, final Set<UserRole> newRoles) {
		try {
			user.setRoles(newRoles);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Changes the set of roles a user possesses.
	 *
	 * @param user           The user whose roles should be changed
	 * @param newRolesString List of new roles
	 * @return if the change was successful.
	 */
	public boolean changeUserRoles(final User user, final List<String> newRolesString) throws UnallowedInputException {
		Set<UserRole> newRolesSet = new HashSet<>();

		user.setRoles(newRolesSet);
		if (newRolesString.size() == 1) {
			switch (newRolesString.get(0).toLowerCase()) {
			case "librarian":
				newRolesSet.add(UserRole.LIBRARIAN);
				break;
			case "admin":
				newRolesSet.add(UserRole.ADMIN);
				break;
			case "customer":
				newRolesSet.add(UserRole.CUSTOMER);
				break;
			default:
				return false;
			}

			user.setRoles(newRolesSet);
			return true;

		} else {
			throw new UnallowedInputException("Multiple roles do not work.");
		}
	}

	/**
	 * Creates a new user and saves it in the user repository.
	 *
	 * @param username  the username for the new user
	 * @param password  the password for the new user
	 * @param firstName the first name of the new user
	 * @param lastName  the last name of the new user
	 * @param enabled   the status of the new user (enabled or disabled)
	 * @param roles     the role for the new user
	 * @param email     the email of the new user
	 *
	 * @return the newly created user
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public User createUser(final String username, final String password, final String firstName, final String lastName,
			final Boolean enabled, final UserRole roles, final String email)
			throws UnauthorizedActionException, UnallowedInputException {

		if (this.getAuthenticatedUser().getRoles().contains(UserRole.LIBRARIAN)
				&& (roles.equals(UserRole.LIBRARIAN) || roles.equals(UserRole.ADMIN))) {
			throw new UnauthorizedActionException("Librarians may not create Admins!");
		}

		// source of following monstrosity:
		// https://stackoverflow.com/questions/201323/how-to-validate-an-email-address-using-a-regular-expression
		String regex = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);

		if (!matcher.matches()) {
			throw new UnallowedInputException("Unallowed input for Email!");
		}

		User createdUser = new User(username, password, firstName, lastName, enabled, roles, email);

		this.saveUser(createdUser);
		return createdUser;
	}

	/**
	 * Deletes the user from the user repository.
	 *
	 * @param user the user to delete
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void deleteUser(final User user) throws UnauthorizedActionException {

		if (this.getAuthenticatedUser().getRoles().contains(UserRole.LIBRARIAN)
				&& user.getRoles().contains(UserRole.ADMIN)) {
			throw new UnauthorizedActionException("Librarians may not delete Administrators!");

		} else if (this.getAuthenticatedUser().getId().equals(user.getId())) {
			throw new UnauthorizedActionException("Users may not delete themself!");

		} else {
			// Check for borrowed articles
			List<Borrowed> still_borrowed = borrowedRepository.findByUser(user);

			if (still_borrowed.size() != 0) {

				throw new UnauthorizedActionException(
						"User cannot be deleted: There is a Media that the user has not returned yet!");
			} else {
				// delete Bookmarks
				List<Bookmark> still_bookmarked = bookmarkRepository.findByUsername(user.getUsername());
				for (Bookmark sbm : still_bookmarked) {
					bookmarkRepository.delete(sbm);
				}
				// delete Reservations
				Collection<Reserved> res = reservedRepository.findByUser(user);
				if (res.size() > 0) {
					for (Reserved r : res) {

						reservedRepository.delete(r);

					}
				}
				mailService.sendMail(user.getEmail(), "Your account has been removed",
						"Hello, your account was deleted by administrative personnel.");
				this.userRepository.delete(user);
			}

		}
	}

	/**
	 * Returns the currently authenticated user
	 *
	 * @return the currently authenticated user
	 */
	public User getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return this.userRepository.findFirstByUsername(auth.getName());
	}

	/**
	 * Filters a collection of users by the given username
	 *
	 * @param filteredUser the collection of users to be filtered
	 * @param username     the username to filter by
	 *
	 * @return the filtered collection of users
	 */
	public Collection<User> filterUserByUsername(final Collection<User> filteredUser, final String username) {
		return filteredUser.stream().filter(x -> x.getUsername().toLowerCase().contains(username))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Filters a collection of users by the given email
	 *
	 * @param filteredUser the collection of users to be filtered
	 * @param email        the email to filter by
	 *
	 * @return the filtered collection of users
	 */
	public Collection<User> filterUserByEmail(final Collection<User> filteredUser, final String email) {
		return filteredUser.stream().filter(x -> x.getEmail().toLowerCase().contains(email))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Filters a collection of users by the given user-role
	 *
	 * @param filteredUser the collection of users to be filtered
	 * @param role         the role to filter by (given as string)
	 *
	 * @return the filtered collection of users
	 */
	public Collection<User> filterUserByRole(final Collection<User> filteredUser, final String role) {
		return filteredUser.stream().filter(x -> x.hasRole(role)).collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Refreshes the user with data from the database
	 *
	 * @param user the user to refresh
	 */
	public void refreshUser(final User user) {
		if(user.isNew()) return;

		User refresh = this.userRepository.findFirstByUsername(user.getUsername());

		user.setFirstName(refresh.getFirstName());
		user.setLastName(refresh.getLastName());
		user.setCreateDate(refresh.getCreateDate());
		user.setUpdateDate(refresh.getUpdateDate());
		user.setEmail(refresh.getEmail());
		user.setEnabled(refresh.isEnabled());
		user.setPassword(refresh.getPassword());
		user.setRoles(refresh.getRoles());
	}

	/**
	 * Custom Exceptions
	 */

	public static class UnauthorizedActionException extends Exception {
		private static final long serialVersionUID = 1L;

		public UnauthorizedActionException(final String message) {
			super(message);
		}
	}

}
