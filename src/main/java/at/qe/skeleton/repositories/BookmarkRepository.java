package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Media;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface BookmarkRepository extends AbstractRepository<Bookmark, Long> {

	Bookmark findFirstByBookmarkID(Long bookmarkID);

//	Bookmark findFirstByUser(User user);

	Bookmark findFirstByMedia(Media media);

	void delete(@Param("bookmark") Bookmark bookmark);


		@Modifying
		@Transactional
		@Query(value = "INSERT INTO BOOKMARK " +
			"(MEDIA_MEDIAID, USER_USERNAME) "
			+ "VALUES " + "(:#{#media.mediaID} , + :username)", nativeQuery = true)
		void add(@Param("media") Media media, @Param("username") String username);




}
