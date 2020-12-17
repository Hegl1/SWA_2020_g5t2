package at.qe.skeleton.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;

import io.micrometer.core.instrument.util.JsonUtils;
import org.springframework.data.domain.Persistable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Entity representing users.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Entity
public class User implements Persistable<String>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 100)
	private String username;
	private boolean enabled;
	private String password;
	private String firstName;
	private String lastName;
	private String email;

	@ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "User_UserRole")
	@Enumerated(EnumType.STRING)
	private Set<UserRole> roles;

	public User(){

	}

	public User(String username, String password, String firstName, String lastName, Boolean enabled, UserRole roles, String email) {

		PasswordEncoder pwEncoder = new BCryptPasswordEncoder(9);

		this.username = username;
		this.password = pwEncoder.encode(password);
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;
		this.roles.add(roles);
		this.email = email;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public Set<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(final UserRole roles){
		this.roles.add(roles);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	@Override
	public String getId() {
		return username;
	}

	@Override
	public boolean isNew() {
		return (username == null);
	}

}
