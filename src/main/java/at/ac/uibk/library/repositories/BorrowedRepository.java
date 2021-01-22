package at.ac.uibk.library.repositories;

import at.ac.uibk.library.model.Borrowed;
import at.ac.uibk.library.model.Media;
import at.ac.uibk.library.model.User;

import java.util.List;

public interface BorrowedRepository extends AbstractRepository<Borrowed, Long> {

	Borrowed findFirstByBorrowID(Long borrowID);

	Borrowed findFirstByUser(User user);

	Borrowed findFirstByMedia(Media media);

	List<Borrowed> findByUser(User user);

	List<Borrowed> findByMedia(Media media);

	Borrowed findFirstByUserAndMedia(User user, Media media);

}
