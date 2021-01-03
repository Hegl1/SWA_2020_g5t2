package at.qe.skeleton.repositories;

import java.util.List;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Media;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends AbstractRepository<Bookmark, Long> {

	Bookmark findFirstByBookmarkID(Long bookmarkID);

	Bookmark findFirstByMedia(Media media);

	Bookmark findFirstByUserAndMedia(User user, Media media);

	@Query(value = "SELECT *" + " FROM BOOKMARK b" + " WHERE b.USER_USERNAME = :user", nativeQuery = true)
	List<Bookmark> findByUsername(@Param("user") String user);
}
