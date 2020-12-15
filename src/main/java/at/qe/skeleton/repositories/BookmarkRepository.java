package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Bookmarks;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;

public interface BookmarkRepository extends AbstractRepository<Bookmarks, Long> {

	Bookmarks findFirstByBookmarkID(Long bookmarkID);

	Bookmarks findFirstByUser(User user);

	Bookmarks findFirstByMedia(Media media);

}
