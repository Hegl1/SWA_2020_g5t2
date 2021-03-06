package at.ac.uibk.library.repositories;

import at.ac.uibk.library.model.User;
import at.ac.uibk.library.model.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for managing {@link User} entities.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
public interface UserRepository extends AbstractRepository<User, Long> {

	User findFirstByUsername(String username);

	List<User> findByUsernameContaining(String username);

	@Query("SELECT u FROM User u WHERE CONCAT(u.firstName, ' ', u.lastName) = :wholeName")
	List<User> findByWholeNameConcat(@Param("wholeName") String wholeName);

	@Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles")
	List<User> findByRole(@Param("role") UserRole role);

}
