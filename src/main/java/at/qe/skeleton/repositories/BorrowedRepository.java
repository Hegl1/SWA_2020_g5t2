package at.qe.skeleton.repositories;

import java.util.List;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;

public interface BorrowedRepository extends AbstractRepository<Borrowed, Long> {

	Borrowed findFirstByBorrowID(Long borrowID);

	Borrowed findFirstByUser(User user);

	Borrowed findFirstByMedia(Media media);

	List<Borrowed> findByUser(User user);

	List<Borrowed> findByMedia(Media media);

	Borrowed findByUserAndMedia(User user, Media media);

}
