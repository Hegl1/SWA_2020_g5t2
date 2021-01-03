package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;

import java.util.List;

public interface BorrowedRepository extends AbstractRepository<Borrowed, Long> {

	Borrowed findFirstByBorrowID(Long borrowID);

	Borrowed findFirstByUser(User user);

	Borrowed findFirstByMedia(Media media);

	List<Borrowed> findByUser(User user);

	List<Borrowed> findByMedia(Media media);

	Borrowed findFirstByUserAndMedia(User user, Media media);

}
