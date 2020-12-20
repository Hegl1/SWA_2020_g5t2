package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends AbstractRepository<Bookmark, Long> {

	Bookmark findFirstByBookmarkID(Long bookmarkID);

//	Bookmark findFirstByUser(User user);

	Bookmark findFirstByMedia(Media media);

}
