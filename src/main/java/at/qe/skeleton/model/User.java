package at.qe.skeleton.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

	@ManyToOne(optional = true)
	private User createUser;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@ManyToOne(optional = true)
	private User updateUser;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "User_UserRole")
	@Enumerated(EnumType.STRING)
	private Set<UserRole> roles;

	public User(){

	}

	public User(final String username, final String password, final String firstName, final String lastName,
				final Boolean enabled, final UserRole roles, final String email) {

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
			this.password = password;
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

	public void setRoles(final Set<UserRole> roles) {
		this.roles = roles;
	}

	public User getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(final User createUser) {
		this.createUser = createUser;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(final Date createDate) {
		this.createDate = createDate;
	}

	public User getUpdateUser() {
		return this.updateUser;
	}

	public void setUpdateUser(final User updateUser) {
		this.updateUser = updateUser;
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
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
