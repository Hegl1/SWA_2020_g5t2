package at.qe.skeleton.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Media;

public interface BookmarkRepository extends AbstractRepository<Bookmark, Long> {

	Bookmark findFirstByBookmarkID(Long bookmarkID);

	Bookmark findFirstByMedia(Media media);

	@Query(value = "SELECT *" + " FROM BOOKMARK b" + " WHERE b.USER_USERNAME = :user", nativeQuery = true)
	List<Bookmark> findByUsername(@Param("user") String user);

}
