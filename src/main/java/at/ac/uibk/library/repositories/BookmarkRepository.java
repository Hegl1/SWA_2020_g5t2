package at.ac.uibk.library.repositories;

import at.ac.uibk.library.model.Bookmark;
import at.ac.uibk.library.model.Media;
import at.ac.uibk.library.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends AbstractRepository<Bookmark, Long> {

	Bookmark findFirstByBookmarkID(Long bookmarkID);

	Bookmark findFirstByMedia(Media media);

	Bookmark findFirstByUserAndMedia(User user, Media media);

	List<Bookmark> findByMedia(Media media);

	@Query(value = "SELECT *" + " FROM BOOKMARK b" + " WHERE b.USER_USERNAME = :user", nativeQuery = true)
	List<Bookmark> findByUsername(@Param("user") String user);
}
