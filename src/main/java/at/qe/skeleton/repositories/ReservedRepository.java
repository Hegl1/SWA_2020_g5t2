package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.Reserved;
import at.qe.skeleton.model.User;

import java.util.List;

public interface ReservedRepository extends AbstractRepository<Reserved, Long> {

	Reserved findFirstByReservedID(Long reservedID);

	Reserved findFirstByUser(User user);

	Reserved findFirstByMedia(Media media);

	List<Reserved> findByUser(User user);

	List<Reserved> findByMedia(Media media);

	Reserved findByUserAndMedia(User user, Media media);

}
