package at.qe.skeleton.repositories;

import java.util.List;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.Reserved;
import at.qe.skeleton.model.User;

public interface ReservedRepository extends AbstractRepository<Reserved, Long> {

	Reserved findFirstByReservedID(Long reservedID);

	Reserved findFirstByUser(User user);

	Reserved findFirstByMedia(Media media);

	List<Reserved> findByUser(User user);

	List<Reserved> findByMedia(Media media);

}
