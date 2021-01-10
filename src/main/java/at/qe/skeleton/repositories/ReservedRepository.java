package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.Reserved;
import at.qe.skeleton.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservedRepository extends AbstractRepository<Reserved, Long> {

	Reserved findFirstByReservedID(Long reservedID);

	Reserved findFirstByUser(User user);

	Reserved findFirstByMedia(Media media);

	List<Reserved> findByUser(User user);

	List<Reserved> findByMedia(Media media);

	Reserved findFirstByUserAndMedia(User user, Media media);

	@Query(value = "SELECT COUNT(*) FROM RESERVED r WHERE r.MEDIA_MEDIAID = :mediaID", nativeQuery = true)
	int getReservationCountForMedia(@Param("mediaID") Long mediaID);
}
