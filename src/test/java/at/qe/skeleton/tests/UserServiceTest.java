package at.qe.skeleton.tests;

import java.util.*;

import at.qe.skeleton.ui.beans.ContextMocker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.services.UserService.UnauthorizedActionException;

import javax.faces.context.FacesContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UserServiceTest {

	@Autowired
	private UserService userService;

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testInitData() {
		Assertions.assertTrue(this.userService.getAllUsers().size() >= 3, "Insufficient amount of users initialized");
		for (User current : this.userService.getAllUsers()) {
			if (current.getUsername().equals("amuss")) {
				Assertions.assertTrue(current.getRoles().contains(UserRole.ADMIN),
						"User \"amuss\" does not have the role ADMIN");
			} else if (current.getUsername().equals("csauer")) {
				Assertions.assertTrue(current.getRoles().contains(UserRole.CUSTOMER),
						"User \"csauer\" does not have the role CUSTOMER");
			} else if (current.getUsername().equals("sprill")) {
				Assertions.assertTrue(current.getRoles().contains(UserRole.LIBRARIAN),
						"User \"sprill\" does not have the role LIBRARIAN");
			}
			Assertions.assertEquals(current.getUpdateDate(), current.getCreateDate(),
					"Create - and UpdateDate do not match at User \"" + current.getUsername() + "\"");
		}
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testAuthorizedSaveUser() {

		User newUser = new User("testUser", "passwd", "Max", "Mustermann", true, UserRole.ADMIN, "tester@email.com");
		userService.saveUser(newUser);
		User loadedNewUser = this.userService.loadUser("testUser");

		Assertions.assertNotNull(loadedNewUser, "New user could not be loaded");
		Assertions.assertNotEquals(loadedNewUser.getPassword(), "passwd", "password was not encrypted");
		Assertions.assertEquals(loadedNewUser.getFirstName(), "Max", "First name was not persisted correctly");
		Assertions.assertEquals(loadedNewUser.getLastName(), "Mustermann", "Last name was not persisted correctly");
		Assertions.assertEquals(loadedNewUser.getEmail(), "tester@email.com");
		Assertions.assertTrue(loadedNewUser.getRoles().contains(UserRole.ADMIN), "User role was not correctly persisted");
	}

	@Test
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedSaveUser() {
		User user = new User("testUser", "passwd", "Max", "Mustermann", true, UserRole.ADMIN, "tester@email.com");

		Assertions.assertThrows(AccessDeniedException.class, () -> this.userService.saveUser(user), "AccessDeniedException was not thrown");
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testDeleteUser() throws UnauthorizedActionException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		User toDeleteUser = this.userService.loadUser("customer2");
		this.userService.deleteUser(toDeleteUser);
		User notExistingUser = this.userService.loadUser("customer2");
		Assertions.assertNull(notExistingUser, "User not deleted properly");

		User userWithBorrows = this.userService.loadUser("csauer");
		Assertions.assertThrows(UnauthorizedActionException.class, () -> this.userService.deleteUser(userWithBorrows));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testSelfDeleteUser() throws UnauthorizedActionException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		User user = this.userService.loadUser("amuss");
		Assertions.assertThrows(UnauthorizedActionException.class, () -> this.userService.deleteUser(user));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "sprill", authorities = { "LIBRARIAN" })
	public void testDeleteUserAsLibrarian() throws UnauthorizedActionException {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		User user = this.userService.loadUser("amuss");
		Assertions.assertThrows(UnauthorizedActionException.class, () -> this.userService.deleteUser(user));
	}

	@Test
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedDeleteUser() throws UnauthorizedActionException {
		Assertions.assertThrows(AccessDeniedException.class, () -> this.userService.deleteUser(this.userService.loadUser("amuss")), "AccessDeniedException was not thrown");
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testUserRoleChange() {
		User user = this.userService.loadUser("csauer");

		List<String> roleList = new ArrayList<>();
		roleList.add("LIBRARIAN");
		this.userService.changeUserRoles(user, roleList);
		Assertions.assertTrue(user.getRoles().contains(UserRole.LIBRARIAN));

		roleList = new ArrayList<>();
		roleList.add("admin");
		this.userService.changeUserRoles(user, roleList);
		Assertions.assertTrue(user.getRoles().contains(UserRole.ADMIN));

		roleList = new ArrayList<>();
		roleList.add("CuStOMeR");
		this.userService.changeUserRoles(user, roleList);
		Assertions.assertTrue(user.getRoles().contains(UserRole.CUSTOMER));

		roleList = new ArrayList<>();
		roleList.add("provokeDefaultCase");
		this.userService.changeUserRoles(user, roleList);
		Assertions.assertTrue(user.getRoles().contains(UserRole.CUSTOMER));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testAuthorizedUserRoleChange() {
		User user = this.userService.loadUser("csauer");
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(UserRole.LIBRARIAN);
		this.userService.changeUserRoles(user, userRoles);

		Assertions.assertTrue(user.getRoles().contains(UserRole.LIBRARIAN));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedUserRoleChange() {
		User user = this.userService.loadUser("csauer");
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(UserRole.LIBRARIAN);

		Assertions.assertThrows(AccessDeniedException.class, () -> this.userService.changeUserRoles(user, userRoles), "AccessDeniedException was not thrown");
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testLoadUser() {
		User user = userService.loadUser("csauer");

		Assertions.assertEquals("Clemens", user.getFirstName(), "Retrieval of first name did not work");
		Assertions.assertEquals("Sauerwein", user.getLastName(), "Retrieval of last name did not work");
		Assertions.assertEquals("c.sauerwein@swa.at", user.getEmail(), "Retrieval of email did not work");
		Assertions.assertTrue(user.getRoles().contains(UserRole.CUSTOMER), "Retrieval of user role did not work");
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testAuthorizedLoadUsers() {
		Collection<User> users = this.userService.getAllUsers();

		Assertions.assertTrue(users.contains(this.userService.loadUser("amuss")));
	}

	@Test
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedLoadUsers() {
		Assertions.assertThrows(AccessDeniedException.class, () -> this.userService.getAllUsers(), "AccessDeniedException was not thrown");
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testAuthorizedGetAllUsers() {
		Collection<User> users = this.userService.getAllUsers();

		Assertions.assertEquals(6, users.size());
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("amuss")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("csauer")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("sprill")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("mfeld")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("lkalt")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("customer2")));
	}

	@Test
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedGetAllUsers() {
		Assertions.assertThrows(AccessDeniedException.class, () -> this.userService.getAllUsers());
	}

	@Test
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testAuthorizedGetAllUsersForAdminAuthority() {
		Collection<User> users = this.userService.getAllUsersForAuthority();

		Assertions.assertEquals(6, users.size());
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("amuss")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("csauer")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("sprill")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("mfeld")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("lkalt")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("customer2")));
	}

	@Test
	@WithMockUser(username = "sprill", authorities = { "LIBRARIAN" })
	public void testAuthorizedGetAllUsersForLibrarianAuthority() {
		Collection<User> users = this.userService.getAllUsersForAuthority();

		Assertions.assertEquals(4, users.size());
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("csauer")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("mfeld")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("lkalt")));
		Assertions.assertTrue(users.stream().anyMatch(user -> user.getId().equals("customer2")));
	}

	@Test
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedGetAllUsersForAuthority() {
		Assertions.assertThrows(AccessDeniedException.class, () -> this.userService.getAllUsersForAuthority());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testAuthorizedLoadUserByName() {
		List<User> users = this.userService.loadUserByName("Clemens Sauerwein");

		Assertions.assertTrue(users.contains(this.userService.loadUser("csauer")));
	}

	@Test
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedLoadUserByName() {
		Assertions.assertThrows(AccessDeniedException.class, () -> this.userService.loadUserByName("Clemens Sauerwein"));
	}

	@Test
	@WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
	public void testLoadCurrentUser() {
		Assertions.assertEquals("csauer", this.userService.loadCurrentUser().getId());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testAuthorizedLoadCustomers() {
		List<User> users = this.userService.loadCustomers();

		Assertions.assertEquals(4, users.size());
		Assertions.assertTrue(users.contains(this.userService.loadUser("csauer")));
		Assertions.assertTrue(users.contains(this.userService.loadUser("mfeld")));
		Assertions.assertTrue(users.contains(this.userService.loadUser("lkalt")));
		Assertions.assertTrue(users.contains(this.userService.loadUser("customer2")));
	}

	@Test
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedLoadCustomers() {
		Assertions.assertThrows(AccessDeniedException.class, () -> this.userService.loadCustomers());
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testAuthorizedCreateCorrectUser() throws UserService.UnallowedInputException, UnauthorizedActionException {
		this.userService.createUser("maxmuster", "passwd", "Max", "Mustermann", true, UserRole.CUSTOMER, "m.mustermann@swa.at");

		Assertions.assertNotNull(this.userService.loadUser("maxmuster"));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testAuthorizedCreateIncorrectUser() throws UserService.UnallowedInputException, UnauthorizedActionException {
		Assertions.assertThrows(UserService.UnallowedInputException.class, () -> this.userService.createUser("maxmuster", "passwd", "Max", "Mustermann", true, UserRole.CUSTOMER, "m.mustermann_ät_swa.at"));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedCreateUser() throws UserService.UnallowedInputException, UnauthorizedActionException {
		Assertions.assertThrows(AccessDeniedException.class, () -> this.userService.createUser("maxmuster", "passwd", "Max", "Mustermann", true, UserRole.CUSTOMER, "m.mustermann@swa.at"));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "sprill", authorities = { "LIBRARIAN" })
	public void testUnauthorizedLibrarianCreateUser() throws UserService.UnallowedInputException, UnauthorizedActionException {
		Assertions.assertThrows(UnauthorizedActionException.class, () -> this.userService.createUser("maxmuster", "passwd", "Max", "Mustermann", true, UserRole.LIBRARIAN, "m.mustermann@swa.at"));
	}

	@Test
	@WithMockUser(username = "amuss", authorities = { "ADMIN" })
	public void testGetAuthenticatedUser() {
		Assertions.assertEquals("amuss", this.userService.getAuthenticatedUser().getId());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testFilterUserByUsername() {
		Collection<User> filteredUsers = this.userService.filterUserByUsername(this.userService.getAllUsers(), "csauer");

		Assertions.assertEquals(1, filteredUsers.size());
		Assertions.assertTrue(filteredUsers.contains(this.userService.loadUser("csauer")));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testFilterUserByEmail() {
		Collection<User> filteredUsers = this.userService.filterUserByEmail(this.userService.getAllUsers(), "c.sauerwein@swa.at");

		Assertions.assertEquals(1, filteredUsers.size());
		Assertions.assertTrue(filteredUsers.contains(this.userService.loadUser("csauer")));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testFilterUserByRole() {
		Collection<User> filteredUsers = this.userService.filterUserByRole(this.userService.getAllUsers(), "LIBRARIAN");

		Assertions.assertEquals(1, filteredUsers.size());
		Assertions.assertTrue(filteredUsers.contains(this.userService.loadUser("sprill")));
	}

}
