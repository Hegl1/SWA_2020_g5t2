package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.Reserved;
import at.qe.skeleton.model.User;

public interface BorrowedRepository extends AbstractRepository<Borrowed, Long> {

	Borrowed findFirstByBorrowID(Long borrowID);

	Reserved findFirstByUser(User user);

	Reserved findFirstByMedia(Media media);

}
