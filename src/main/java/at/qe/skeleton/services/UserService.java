package at.qe.skeleton.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
	@PreAuthorize("hasAuthority('ADMIN')")
	public Collection<User> getAllUsers() {
		return userRepository.findAll();
	}

	/**
	 * Loads a single user identified by its username.
	 *
	 * @param username the username to search for
	 * @return the user with the given username
	 */
	@PreAuthorize("hasAuthority('ADMIN') or principal.username eq #username")
	public User loadUser(final String username) {
		return userRepository.findFirstByUsername(username);
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
	@PreAuthorize("hasAuthority('ADMIN')")
	public User saveUser(final User user) {
		return userRepository.save(user);
	}

	/**
	 * Deletes the user.
	 *
	 * @param user the user to delete
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	public void deleteUser(final User user) {
		userRepository.delete(user);
		// :TODO: write some audit log stating who and when this user was permanently
		// deleted.
	}

	private User getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findFirstByUsername(auth.getName());
	}

}
