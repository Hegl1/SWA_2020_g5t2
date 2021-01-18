package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Entity representing users. Very similar to the User class of the skeleton
 * project.
 * 
 * @author Marcel Huber
 * @version 1.0
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

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "User_UserRole")
	@Enumerated(EnumType.STRING)
	private Set<UserRole> roles;

	/**
	 * Default construcotr. Initializes the set of user roles.
	 */
	public User() {
		this.roles = new HashSet<UserRole>();
		this.updateDate = new Date();
		this.createDate = new Date();
	}

	/**
	 * Constructor that initializes most of the fields of the user.
	 * 
	 * @param username  the username for the user
	 * @param password  the password for the user
	 * @param firstName the first name of the user
	 * @param lastName  the last name of the user
	 * @param enabled   boolean for whether the user is enabled
	 * @param roles     one role to add to the set of roles
	 * @param email     email of the user
	 */
	public User(final String username, final String password, final String firstName, final String lastName,
			final Boolean enabled, final UserRole roles, final String email) {

		this();
		this.username = username;
		this.setPassword(password);
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;
		this.roles.add(roles);
		this.email = email;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		if (password != null && password != "") {

			// if password was changed, then encrypt it again
			Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
			if (!BCRYPT_PATTERN.matcher(password).matches()) {
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				this.password = passwordEncoder.encode(password);
			}
		}
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public Set<UserRole> getRoles() {
		return this.roles;
	}

	public boolean hasRole(String role){
		switch(role){
			case "ADMIN": return getRoles().contains(UserRole.ADMIN);
			case "CUSTOMER": return getRoles().contains(UserRole.CUSTOMER);
			case "LIBRARIAN": return getRoles().contains(UserRole.LIBRARIAN);
		}

		return false;
	}

	public void setRoles(final Set<UserRole> roles) {
		this.roles = roles;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(final Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(final Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	@Override
	public String getId() {
		return this.username;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "at.qe.sepm.skeleton.model.User[ id=" + username + " ]";
	}

	@Override
	public boolean isNew() {
		return (this.username == null);
	}

	@PrePersist
	void prePersist() {
		this.createDate = this.updateDate = new Date();
	}

	@PreUpdate
	void preUpdate() {
		this.updateDate = new Date();
	}

}
