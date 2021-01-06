package at.qe.skeleton.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.services.UserService.UnauthorizedActionException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UserServiceTest {

	@Autowired
	UserService userService;

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testInitData() {
		Assertions.assertTrue(userService.getAllUsers().size() >= 3, "Insufficient amoutn of users initialized");
		for (User current : userService.getAllUsers()) {
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
	public void testSaveUser() {

		User newUser = new User("testUser", "passwd", "Max", "Mustermann", true, UserRole.ADMIN, "tester@email.com");
		userService.saveUser(newUser);
		User loadedNewUser = userService.loadUser("testUser");
		Assertions.assertNotNull(loadedNewUser, "New user could not be loaded");
		Assertions.assertNotEquals(loadedNewUser.getPassword(), "passwd", "password was not encrypted");
		Assertions.assertEquals(loadedNewUser.getFirstName(), "Max", "First name was not persisted correctly");
		Assertions.assertEquals(loadedNewUser.getLastName(), "Mustermann", "Last name was not persisted correctly");
		Assertions.assertEquals(loadedNewUser.getEmail(), "tester@email.com");
		Assertions.assertTrue(loadedNewUser.getRoles().contains(UserRole.ADMIN),
				"Userrole was not correctly persisted");
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testDeleteUser() throws UnauthorizedActionException {
		// TODO find a proper solution
		UserService mockService = Mockito.mock(UserService.class);
		Mockito.when(mockService.getAuthenticatedUser()).thenReturn(userService.loadUser("amuss"));
		User toDeleteUser = userService.loadUser("csauer");
		mockService.deleteUser(toDeleteUser);
		User notExistingUser = mockService.loadUser("csauer");
		Assertions.assertNull(notExistingUser, "User not deleted properly");
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testAuthorizedUserRoleChange() {
		User testUser = userService.loadUser("csauer");
		List<String> roleList = new ArrayList<String>();
		roleList.add("LIBRARIAN");
		userService.changeUserRoles(testUser, roleList);
		Assertions.assertTrue(testUser.getRoles().contains(UserRole.LIBRARIAN));
	}

	@Test(expected = org.springframework.security.access.AccessDeniedException.class)
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedUserRoleCahnge() {
		User testUser = userService.loadUser("csauer");
		List<String> roleList = new ArrayList<String>();
		roleList.add("LIBRARIAN");
		Assertions.assertFalse(userService.changeUserRoles(testUser, roleList),
				"Changing roles should not work without authorizatoin");
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testAuthorizedLoadUser() {
		User testUser = userService.loadUser("csauer");
		Assertions.assertEquals("Clemens", testUser.getFirstName(), "Retrival of first name didnt work");
		Assertions.assertEquals("Sauerwein", testUser.getLastName(), "Retrival of last name didnt work");
		Assertions.assertEquals("c.sauerwein@swa.at", testUser.getEmail());
		Assertions.assertTrue(testUser.getRoles().contains(UserRole.CUSTOMER));
	}

	@Test(expected = org.springframework.security.access.AccessDeniedException.class)
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedLoadUser() {
		Assertions.assertNull(userService.loadUser("amuss"));
	}

	@Test(expected = org.springframework.security.access.AccessDeniedException.class)
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedSaveUser() {
		User testUser = new User("testUser", "passwd", "Max", "Mustermann", true, UserRole.ADMIN, "tester@email.com");
		Assertions.assertNotNull(userService.saveUser(testUser));
	}

	@Test(expected = org.springframework.security.access.AccessDeniedException.class)
	@WithMockUser(username = "customer", authorities = { "CUSTOMER" })
	public void testUnauthorizedDeleteUser() throws UnauthorizedActionException {
		userService.deleteUser(userService.loadUser("amuss"));
		Assert.fail();
	}

	@Test(expected = org.springframework.security.access.AccessDeniedException.class)
	@WithMockUser(username = "admin", authorities = { "CUSTOMER" })
	public void testUnauthorizedLoadUsers() {
		Assertions.assertNull(userService.getAllUsers());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testAuthorizedLoadUsers() {
		Collection<User> userCollection = userService.getAllUsers();
		Assertions.assertTrue(userCollection.contains(userService.loadUser("amuss")));
	}

}
